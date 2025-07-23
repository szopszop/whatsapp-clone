package tracz.messageservice.service;

import org.springframework.data.domain.Page;
import tracz.messageservice.dto.MessageDTO;
import tracz.messageservice.dto.SendMessageRequestDTO;

import java.util.UUID;

public interface MessageService {


    /**
     * Saves a new message and returns its data transfer object.
     *
     * @param requestDTO The DTO containing the message details.
     * @param senderId The authenticated ID of the message sender.
     * @return The DTO of the saved message.
     */
    MessageDTO sendMessage(SendMessageRequestDTO requestDTO, UUID senderId);

    /**
     * Retrieves a paginated list of messages for a given conversation.
     *
     * @param conversationId The ID of the conversation.
     * @param page The page number to retrieve.
     * @param size The number of messages per page.
     * @return A page of message DTOs.
     */
    Page<MessageDTO> getMessagesForConversation(UUID conversationId, int page, int size);
}
