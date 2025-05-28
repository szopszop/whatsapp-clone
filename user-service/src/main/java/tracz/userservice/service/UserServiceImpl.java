package tracz.userservice.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.User;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;
import tracz.userservice.config.ExceptionMessages;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
        return UserMapper.userToDto(user);
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

        Page<User> userPage = Optional.ofNullable(email)
                .filter(e -> !e.trim().isEmpty())
                .map(e -> userRepository.findByEmailContainingIgnoreCase(e, pageRequest))
                .orElseGet(() -> userRepository.findAll(pageRequest));

        return userPage.map(UserMapper::userToDto);
    }

}
