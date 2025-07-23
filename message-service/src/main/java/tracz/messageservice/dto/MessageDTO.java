package tracz.messageservice.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDTO (String id, UUID conversationId, UUID senderId,
                          UUID recipientId, String content, Instant createdAt){
}
