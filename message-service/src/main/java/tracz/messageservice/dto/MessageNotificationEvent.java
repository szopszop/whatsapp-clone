package tracz.messageservice.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageNotificationEvent(
        String messageId,
        UUID conversationId,
        UUID senderId,
        UUID recipientId,
        String content,
        Instant createdAt
) {
}
