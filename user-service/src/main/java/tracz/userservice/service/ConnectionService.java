package tracz.userservice.service;

import tracz.userservice.dto.ConnectionResponseDTO;
import tracz.userservice.dto.UserResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing user connections
 */
public interface ConnectionService {

    /**
     * Send a connection request to another user
     *
     * @param requesterId The ID of the user sending the request
     * @param targetId The ID of the user receiving the request
     * @return The created connection request
     */
    ConnectionResponseDTO sendConnectionRequest(UUID requesterId, UUID targetId);

    /**
     * Accept a connection request
     *
     * @param userId The ID of the user accepting the request
     * @param requestId The ID of the connection request
     * @return The updated connection
     */
    ConnectionResponseDTO acceptConnectionRequest(UUID userId, UUID requestId);

    /**
     * Reject a connection request
     *
     * @param userId The ID of the user rejecting the request
     * @param requestId The ID of the connection request
     */
    void rejectConnectionRequest(UUID userId, UUID requestId);

    /**
     * Get all connections for a user
     *
     * @param userId The ID of the user
     * @return List of connected users
     */
    List<UserResponseDTO> getUserConnections(UUID userId);

    /**
     * Get all pending connection requests for a user
     *
     * @param userId The ID of the user
     * @return List of pending connection requests
     */
    List<ConnectionResponseDTO> getPendingConnectionRequests(UUID userId);

    /**
     * Remove a connection
     *
     * @param userId The ID of the user removing the connection
     * @param connectionId The ID of the connection to remove
     */
    void removeConnection(UUID userId, UUID connectionId);
}