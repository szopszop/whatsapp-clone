package tracz.authserver.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tracz.authserver.dto.*;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.entity.Role;
import tracz.authserver.exception.UserAlreadyExistsException;
import tracz.authserver.exception.UserNotFoundException;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.AuthUserRepository;
import tracz.authserver.repository.RoleRepository;
import java.util.Set;
import java.util.UUID;
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
    private final UserEventPublisher userEventPublisher;

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

        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
        userEventPublisher.publishUserRegistered(event);

        return AuthUserMapper.authUserToDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID authUserId) {
        if (!authUserRepository.existsById(authUserId)) {
            throw new UserNotFoundException("User with id " + authUserId + " not found in auth-server.");
        }
        authUserRepository.deleteById(authUserId);
        log.info("User with id {} deleted from auth-server.", authUserId);

        userEventPublisher.publishUserDeleted(new UserDeletedEvent(authUserId));
    }
}
