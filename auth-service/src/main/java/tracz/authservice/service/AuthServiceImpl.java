package tracz.authservice.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tracz.authservice.dto.*;
import tracz.authservice.repository.AuthUserRepository;
import tracz.commonservice.config.ExceptionMessages;
import tracz.userservice.dto.RegistrationRequest;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.Role;
import tracz.userservice.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;


    @Override
    public UserDTO register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(ExceptionMessages.EMAIL_EXISTS);
        }
        log.info("Registering user with email: {}", request.getEmail());
        User user = User.builder()
                .email(request.getEmail())
                .role(Role.USER)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        UserDTO savedDTO = userMapper.userToDto(userRepository.saveAndFlush(user));
        log.debug("Registered user with email: {}", savedDTO.getEmail());
        return savedDTO;
    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
