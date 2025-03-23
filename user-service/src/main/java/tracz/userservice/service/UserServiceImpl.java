package tracz.userservice.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.Role;
import tracz.userservice.entity.User;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder ;
    private final UserMapper userMapper;

    @Override
    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        log.info("Registering user with email: {}", request.getEmail());
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .role(Role.USER)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return userMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
