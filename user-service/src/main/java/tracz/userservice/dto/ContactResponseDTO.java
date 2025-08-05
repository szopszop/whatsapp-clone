package tracz.userservice.dto;

import java.util.UUID;

public record ContactResponseDTO(
    UUID id,
    UserResponseDTO contact,
    UUID conversationId
) {}
