package tracz.messageservice.dto;

import java.util.UUID;

public record SendMessageRequestDTO(UUID conversationId, UUID recipientId, String content) {}
