package tracz.userservice.dto;

import java.util.UUID;

/**
 * DTO for sending a connection request
 */
public record ConnectionRequestDTO(UUID targetUserId) {
}