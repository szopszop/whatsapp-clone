package tracz.authserver.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.*;
import tracz.authserver.entity.*;
import tracz.authserver.exception.ServiceUnavailableException;
import tracz.authserver.exception.UserAlreadyExistsException;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.*;
import tracz.authserver.service.client.UserServiceFeignClient;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Tag(
        name = "CRUD REST APIs for Auth Server - Whatsapp",
        description = "Register, login, token refresh"
)
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserServiceFeignClient userServiceFeignClient;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    @Transactional
    public AuthUserDTO register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.email());
        }
        log.info("Registering user with email: {}", request.email());
        AuthUser authUser = AuthUser.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER does not exist in DB"));
        authUser.setRoles(Set.of(userRole));

        AuthUser savedUser = authUserRepository.save(authUser);
        log.info("User with email {} registered successfully in auth-server. ID: {}", savedUser.getEmail(),
                savedUser.getId());

        provisionUserInUserService(savedUser);
        return AuthUserMapper.authUserToDto(savedUser);
    }

    private void provisionUserInUserService(AuthUser authUser) {
        UserProvisionRequestDTO provisionRequestDTO = new UserProvisionRequestDTO(
                authUser.getId(),
                authUser.getEmail(),
                authUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        log.info("Attempting to provision user {} in user-service via Feign Client.", authUser.getEmail());

        try {
            ResponseEntity<Void> responseEntity = userServiceFeignClient.provisionUser(provisionRequestDTO);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("User {} provisioned successfully in user-service. Status: {}",
                        authUser.getEmail(), responseEntity.getStatusCode());
            } else {
                log.error("Failed to provision user {} in user-service (Feign response not 2xx). Status: {}",
                        authUser.getEmail(), responseEntity.getStatusCode());
                throw new ServiceUnavailableException(
                        "Failed to provision user in user-service. Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            log.error(
                    "Error during user provisioning call for user {} to user-service (Feing Exception or Fallback issue): {}",
                    authUser.getEmail(), e.getMessage(), e);
            throw new ServiceUnavailableException(
                    "User provisioning failed for " + authUser.getEmail() + " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );
        return generateTokens(authentication);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            Jwt jwt = jwtDecoder.decode(request.refreshToken());
            String jwtId = jwt.getId();

            if (jwtId == null || blacklistedTokenRepository.existsByJwtId(jwtId)) {
                throw new AuthenticationException(ExceptionMessages.INVALID_TOKEN) {
                };
            }
            String email = jwt.getSubject();

            AuthUser authUser = authUserRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));

            Collection<? extends GrantedAuthority> authorities = authUser.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email, null, authorities);

            return generateTokens(authentication);

        } catch (JwtException e) {
            throw new AuthenticationException(ExceptionMessages.INVALID_TOKEN, e) {
            };
        }
    }

    private AuthResponse generateTokens(Authentication authentication) {
        Instant now = Instant.now();
        String subject = authentication.getName();
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .claim("scope", authorities)
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, accessClaims)).getTokenValue();
        String refreshedToken = jwtEncoder.encode(JwtEncoderParameters.from(header, refreshClaims)).getTokenValue();

        return new AuthResponse(accessToken, refreshedToken);
    }

    @Transactional
    @Override
    public void logout(RefreshTokenRequest request) {
        try {
            Jwt jwt = jwtDecoder.decode(request.refreshToken());
            String jwtId = jwt.getId();
            Instant expiry = jwt.getExpiresAt();

            if (blacklistedTokenRepository.existsByJwtId(jwtId)) {
                log.info("Token with jwtId {} has been blacklisted already.", jwtId);
                return;
            }

            if (jwtId == null || expiry != null) {
                BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                        .jwtId(jwtId)
                        .expiryDate(expiry)
                        .build();
                blacklistedTokenRepository.save(blacklistedToken);
                log.info("Token with jwtId {} has been blacklisted.", jwtId);
            }

        } catch (JwtException e) {
            log.warn("Attempted to logout with invalid token: {}", e.getMessage());
        }
    }
}
