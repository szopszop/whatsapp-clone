package tracz.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracz.userservice.dto.ConnectionResponseDTO;
import tracz.userservice.dto.UserResponseDTO;
import tracz.userservice.entity.Connection;
import tracz.userservice.entity.Connection.ConnectionStatus;
import tracz.userservice.entity.User;
import tracz.userservice.exception.BadRequestException;
import tracz.userservice.exception.ResourceNotFoundException;
import tracz.userservice.mapper.ConnectionMapper;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.ConnectionRepository;
import tracz.userservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ConnectionResponseDTO sendConnectionRequest(UUID requesterId, UUID targetId) {
        if (requesterId.equals(targetId)) {
            throw new BadRequestException("You cannot send a connection request to yourself");
        }

        // Check if users exist
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester user not found"));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        // Check if a connection already exists
        connectionRepository.findByRequesterIdAndTargetId(requesterId, targetId)
                .ifPresent(connection -> {
                    throw new BadRequestException("A connection request already exists between these users");
                });

        // Also check the reverse direction
        connectionRepository.findByRequesterIdAndTargetId(targetId, requesterId)
                .ifPresent(connection -> {
                    throw new BadRequestException("A connection request already exists from the target user");
                });

        // Create new connection request
        Connection connection = Connection.builder()
                .requesterId(requesterId)
                .targetId(targetId)
                .status(ConnectionStatus.PENDING)
                .build();

        Connection savedConnection = connectionRepository.save(connection);
        log.info("Created connection request from {} to {}", requesterId, targetId);

        // Create a map of user IDs to User entities for the mapper
        Map<UUID, User> userMap = new HashMap<>();
        userMap.put(requester.getId(), requester);
        userMap.put(target.getId(), target);

        return ConnectionMapper.connectionToDto(savedConnection, userMap);
    }

    @Override
    @Transactional
    public ConnectionResponseDTO acceptConnectionRequest(UUID userId, UUID requestId) {
        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        // Verify the user is the target of the request
        if (!connection.getTargetId().equals(userId)) {
            throw new BadRequestException("You are not authorized to accept this connection request");
        }

        // Verify the request is pending
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("This connection request is not pending");
        }

        // Update the connection status
        connection.setStatus(ConnectionStatus.ACCEPTED);
        Connection updatedConnection = connectionRepository.save(connection);
        log.info("Accepted connection request {} from {} to {}", requestId, connection.getRequesterId(), userId);

        // Get user information for the response
        User requester = userRepository.findById(connection.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester user not found"));
        User target = userRepository.findById(connection.getTargetId())
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        Map<UUID, User> userMap = new HashMap<>();
        userMap.put(requester.getId(), requester);
        userMap.put(target.getId(), target);

        return ConnectionMapper.connectionToDto(updatedConnection, userMap);
    }

    @Override
    @Transactional
    public void rejectConnectionRequest(UUID userId, UUID requestId) {
        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        // Verify the user is the target of the request
        if (!connection.getTargetId().equals(userId)) {
            throw new BadRequestException("You are not authorized to reject this connection request");
        }

        // Verify the request is pending
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("This connection request is not pending");
        }

        // Update the connection status
        connection.setStatus(ConnectionStatus.REJECTED);
        connectionRepository.save(connection);
        log.info("Rejected connection request {} from {} to {}", requestId, connection.getRequesterId(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUserConnections(UUID userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        // Get all accepted connections where the user is either the requester or the target
        List<Connection> connections = connectionRepository.findAllAcceptedConnectionsByUserId(userId);

        if (connections.isEmpty()) {
            return new ArrayList<>();
        }

        // Extract the IDs of the connected users
        List<UUID> connectedUserIds = connections.stream()
                .map(connection -> connection.getRequesterId().equals(userId) 
                        ? connection.getTargetId() 
                        : connection.getRequesterId())
                .collect(Collectors.toList());

        // Get the user information for all connected users
        List<User> connectedUsers = userRepository.findAllById(connectedUserIds);

        // Map to DTOs
        return connectedUsers.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionResponseDTO> getPendingConnectionRequests(UUID userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        // Get all pending connection requests sent to the user
        List<Connection> pendingRequests = connectionRepository.findByTargetIdAndStatus(userId, ConnectionStatus.PENDING);

        if (pendingRequests.isEmpty()) {
            return new ArrayList<>();
        }

        // Get all user IDs involved in the requests
        List<UUID> userIds = new ArrayList<>();
        for (Connection connection : pendingRequests) {
            userIds.add(connection.getRequesterId());
            userIds.add(connection.getTargetId());
        }

        // Get user information
        List<User> users = userRepository.findAllById(userIds);
        Map<UUID, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // Map to DTOs
        return pendingRequests.stream()
                .map(connection -> ConnectionMapper.connectionToDto(connection, userMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeConnection(UUID userId, UUID connectionId) {
        Connection connection = connectionRepository.findByIdAndRequesterIdOrIdAndTargetId(
                connectionId, userId, connectionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found or you are not part of this connection"));

        // Only accepted connections can be removed
        if (connection.getStatus() != ConnectionStatus.ACCEPTED) {
            throw new BadRequestException("This connection is not in an accepted state");
        }

        // Delete the connection
        connectionRepository.delete(connection);
        log.info("Removed connection {} between {} and {}", connectionId, connection.getRequesterId(), connection.getTargetId());
    }
}
