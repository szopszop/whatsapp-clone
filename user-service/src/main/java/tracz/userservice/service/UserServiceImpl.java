package tracz.userservice.service;

import java.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import tracz.userservice.dto.*;
import tracz.userservice.entity.User;
import tracz.userservice.entity.UserStatus;
import tracz.userservice.exception.ResourceNotFoundException;
import tracz.userservice.exception.UserAlreadyExistsException;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;
import tracz.userservice.config.ExceptionMessages;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher; // Wstrzyknięty publisher


    @Override
    public UserResponseDTO findByAuthServerUserId(UUID id) {
        User user = userRepository.findByAuthServerUserId(id)
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
    @Transactional
    public UserResponseDTO createUser(UserCreationRequestDTO creationRequest) {
        log.info("New user creation inside user-service for email: {} (authId: {})",
                creationRequest.email(), creationRequest.authUserId());
        if (existsByEmail(creationRequest.email())) {
            log.warn("User {} already exists in DB", creationRequest.email());
            throw new UserAlreadyExistsException("User with email " + creationRequest.email() + " already exists in DB");
        }
        if (userRepository.existsByAuthServerUserId(creationRequest.authUserId())) {
            log.warn("User {} with authServerId {} already exists in DB ",
                    creationRequest.email(), creationRequest.authUserId());
            throw new UserAlreadyExistsException("User with authServerId " + creationRequest.authUserId() + " already exists in DB");
        }
        User savedNewUser = userRepository.save(UserMapper.dtoToUser(creationRequest));
        log.info("User {} (authId:  {}) created successfully",
                creationRequest.email(), creationRequest.authUserId());
        return UserMapper.userToDto(savedNewUser);
    }

    @Override
    public Page<UserResponseDTO> searchUsers(String email, Integer pageNumber, Integer pageSize) {
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
    public UserResponseDTO updateMyProfile(UUID authUserId, UserProfileUpdateDTO updateDTO) {
        User userToUpdate = findUserByAuthIdOrThrow(authUserId);
        UserMapper.updateFromDto(updateDTO, userToUpdate);
        User updatedUser = userRepository.save(userToUpdate);
        log.info("Profile for user {} updated.", updatedUser.getEmail());
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID authUserId, UserStatusUpdateDTO statusUpdateDTO) {
        User user = findUserByAuthIdOrThrow(authUserId);
        user.setStatus(UserStatus.valueOf(statusUpdateDTO.status()));
        userRepository.save(user);
        log.info("Status for user {} updated to {}.", user.getEmail(), statusUpdateDTO.status());

        userEventPublisher.publishUserStatusUpdated(
                new UserStatusUpdatedEvent(authUserId, statusUpdateDTO.status())
        );
    }

    @Override
    @Transactional
    public void deleteUserByAuthId(UUID authUserId) {
        User user = findUserByAuthIdOrThrow(authUserId);
        userRepository.delete(user);
        log.info("User with email {} (authId: {}) deleted successfully from user-service.",
                user.getEmail(), authUserId);
    }

    @Override
    public void addFcmToken(UUID userId, String token) {

    }

    @Override
    public Set<String> getFcmTokens(UUID userId) {
        return Set.of();
    }

    private User findUserByAuthIdOrThrow(UUID authId) {
        return userRepository.findByAuthServerUserId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with authId: " + authId));
    }
}
