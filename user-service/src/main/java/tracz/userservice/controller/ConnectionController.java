package tracz.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.dto.ConnectionRequestDTO;
import tracz.userservice.dto.ConnectionResponseDTO;
import tracz.userservice.dto.UserResponseDTO;
import tracz.userservice.service.ConnectionService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    /**
     * Send a connection request to another user
     */
    @PostMapping("/request")
    public ResponseEntity<ConnectionResponseDTO> sendConnectionRequest(
            @RequestBody ConnectionRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ConnectionResponseDTO response = connectionService.sendConnectionRequest(userId, requestDTO.targetUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Accept a connection request
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ConnectionResponseDTO> acceptConnectionRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ConnectionResponseDTO response = connectionService.acceptConnectionRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject a connection request
     */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectConnectionRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        connectionService.rejectConnectionRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all connections for the current user
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getMyConnections(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<UserResponseDTO> connections = connectionService.getUserConnections(userId);
        return ResponseEntity.ok(connections);
    }

    /**
     * Get all pending connection requests for the current user
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponseDTO>> getPendingConnectionRequests(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<ConnectionResponseDTO> pendingRequests = connectionService.getPendingConnectionRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    /**
     * Remove a connection
     */
    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> removeConnection(
            @PathVariable UUID connectionId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        connectionService.removeConnection(userId, connectionId);
        return ResponseEntity.noContent().build();
    }
}