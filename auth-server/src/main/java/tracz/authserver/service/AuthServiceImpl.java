package tracz.authserver.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.*;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.AuthUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUserMapper authUserMapper;
    private final JwtDecoder jwtDecoder;
    //private final UserClient


    @Transactional
    public AuthUserDTO register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(ExceptionMessages.EMAIL_EXISTS);        }
        log.info("Registering user with email: {}", request.getEmail());
        AuthUser authUser = AuthUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(List.of("ROLE_USER")))
                .build();

        AuthUserDTO savedDTO = authUserMapper.authUserToDto(authUserRepository.saveAndFlush(authUser));
        log.info("Registered user with email: {}", savedDTO.getEmail());
        return savedDTO;
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        return generateTokens(authentication);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            Jwt jwt = jwtDecoder.decode(request.getRefreshToken());
            String email = jwt.getSubject();

            AuthUser authUser = authUserRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));

            Collection<? extends GrantedAuthority> authorities = authUser.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email, null, authorities);

            return generateTokens(authentication);

        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token", e);
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
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, accessClaims)).getTokenValue();
        String refreshedToken = jwtEncoder.encode(JwtEncoderParameters.from(header, refreshClaims)).getTokenValue();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshedToken)
                .build();
    }
}
