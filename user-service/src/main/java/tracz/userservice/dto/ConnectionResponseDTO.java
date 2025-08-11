package tracz.userservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for connection request response
 */
public record ConnectionResponseDTO(
    UUID id,
    UUID requesterId,
    UUID targetId,
    String requesterName,
    String targetName,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}