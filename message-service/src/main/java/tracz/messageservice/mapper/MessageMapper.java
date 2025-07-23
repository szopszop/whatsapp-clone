package tracz.messageservice.mapper;

import org.springframework.stereotype.Component;
import tracz.messageservice.dto.MessageDTO;
import tracz.messageservice.entity.Message;

@Component
public class MessageMapper {

    public Message toEntity(MessageDTO dto) {
        return Message.builder()
                .id(dto.id())
                .senderId(dto.senderId())
                .recipientId(dto.recipientId())
                .content(dto.content())
                .createdAt(dto.createdAt())
                .build();
    }

    public MessageDTO toDto(Message entity) {
        return new MessageDTO (
                entity.getId(),
                entity.getConversationId(),
                entity.getSenderId(),
                entity.getRecipientId(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

}
