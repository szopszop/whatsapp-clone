package tracz.messageservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.lang.annotation.Documented;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @Field("conversation_id")
    private UUID conversationId;

    @Field("sender_id")
    private UUID senderId;

    @Field("recipient_id")
    private UUID recipientId;

    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    @Field("content")
    private String content;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
}
