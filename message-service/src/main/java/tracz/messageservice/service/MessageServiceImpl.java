package tracz.messageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tracz.messageservice.dto.MessageDTO;
import tracz.messageservice.dto.SendMessageRequestDTO;
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
    private final SimpMessagingTemplate messagingTemplate;

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

        messagingTemplate.convertAndSendToUser(
                requestDTO.recipientId().toString(),
                "/queue/messages",
                messageDTO
        );
        log.info("Message {} sent to recipient {} via WebSocket", savedMessage.getId(), requestDTO.recipientId());

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
