package tracz.userservice.service;

import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import tracz.userservice.dto.UserCreationRequestDTO;
import tracz.userservice.dto.UserResponseDTO;
import tracz.userservice.entity.User;
import tracz.userservice.exception.ResourceNotFoundException;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;
import tracz.userservice.config.ExceptionMessages;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
        return UserMapper.userToDto(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserResponseDTO> getUsers(String email, Integer pageNumber, Integer pageSize) {
        int defaultPage = 0;
        int defaultSize = 25;

        int validPage = (pageNumber == null || pageNumber < 0) ? defaultPage : pageNumber;
        int validSize = (pageSize == null || pageSize <= 0) ? defaultSize : pageSize;

        PageRequest pageRequest = PageRequest.of(validPage, validSize, Sort.by(Sort.Direction.ASC, "email"));

        Page<User> userPage = Optional.ofNullable(email)
                .filter(e -> !e.trim().isEmpty())
                .map(e -> userRepository.findByEmailContainingIgnoreCase(e, pageRequest))
                .orElseGet(() -> userRepository.findAll(pageRequest));

        return userPage.map(UserMapper::userToDto);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationRequestDTO creationRequest) {
        log.info("New user creation inside user-service for email: {} (authId: {})",
                creationRequest.email(), creationRequest.authUserId());
        if (existsByEmail(creationRequest.email())) {
            log.warn("User {} already exists in DB", creationRequest.email());
        }
        if (userRepository.existsByAuthServerUserId(creationRequest.authUserId())) {
            log.warn("User {} with authServerId {} already exists in DB ",
                    creationRequest.email(), creationRequest.authUserId());
        }
        User savedNewUser = userRepository.save(UserMapper.dtoToUser(creationRequest));
        log.info("User {} (authId:  {}) created successfully",
                creationRequest.email(), creationRequest.authUserId());
        return UserMapper.userToDto(savedNewUser);
    }

}
