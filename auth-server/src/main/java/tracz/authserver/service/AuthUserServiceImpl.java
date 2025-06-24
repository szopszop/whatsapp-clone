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
    private final UserServiceFeignClient userServiceFeignClient;

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
                    "Error during user provisioning call for user {} to user-service (Feign Exception or Fallback issue): {}",
                    authUser.getEmail(), e.getMessage(), e);
            throw new ServiceUnavailableException(
                    "User provisioning failed for " + authUser.getEmail() + " due to: " + e.getMessage(), e);
        }
    }

}
