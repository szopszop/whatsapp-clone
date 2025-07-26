package tracz.messageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tracz.messageservice.dto.*;
import tracz.messageservice.entity.Message;
import tracz.messageservice.mapper.MessageMapper;
import tracz.messageservice.repository.MessageRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final RabbitTemplate rabbitTemplate;

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String MESSAGE_ROUTING_KEY = "message.sent";

    @Override
    public MessageDTO sendMessage(SendMessageRequestDTO requestDTO, UUID senderId) {
        Message messageToSave = Message.builder()
                .conversationId(requestDTO.conversationId())
                .senderId(senderId)
                .recipientId(requestDTO.recipientId())
                .content(requestDTO.content())
                .build();

        Message savedMessage = messageRepository.save(messageToSave);
        log.info("Saved message {} from sender {}", savedMessage.getId(), senderId);

        MessageDTO messageDTO = messageMapper.toDto(savedMessage);

        MessageNotificationEvent event = new MessageNotificationEvent(
                messageDTO.id(),
                messageDTO.conversationId(),
                messageDTO.senderId(),
                messageDTO.recipientId(),
                messageDTO.content(),
                messageDTO.createdAt()
        );

        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, MESSAGE_ROUTING_KEY, event);
        log.info("Published notification event for message id: {}", savedMessage.getId());
        return messageDTO;
    }

    @Override
    public Page<MessageDTO> getMessagesForConversation(UUID conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
        log.info("Found {} messages for conversation {}", messagePage.getTotalElements(), conversationId);
        return messagePage.map(messageMapper::toDto);
    }

}
