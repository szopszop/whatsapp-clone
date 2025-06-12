package tracz.userservice.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        UUID authUserId,
        String email,
        Set<String> roles,
        Instant createdAt,
        String firstName,
        String lastName
) {}
