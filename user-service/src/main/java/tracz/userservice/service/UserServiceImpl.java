package tracz.userservice.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tracz.userservice.config.ExceptionMessages;
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
    public UserDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserDTO> getUsers(String email, Integer pageNumber, Integer pageSize) {
        int defaultPage = 0;
        int defaultSize = 25;

        int validPage = (pageNumber == null || pageNumber < 0) ? defaultPage : pageNumber;
        int validSize = (pageSize == null || pageSize <= 0) ? defaultSize : pageSize;

        PageRequest pageRequest = PageRequest.of(validPage, validSize, Sort.by(Sort.Direction.ASC, "email"));

        Page<User> userPage;
        if (email != null && !email.trim().isEmpty()) {
            userPage = userRepository.findByEmailContainingIgnoreCase(email, pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        return userPage.map(userMapper::userToDto);
    }

}
