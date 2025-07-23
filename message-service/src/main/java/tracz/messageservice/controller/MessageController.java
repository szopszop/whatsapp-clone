package tracz.messageservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tracz.messageservice.dto.MessageDTO;
import tracz.messageservice.dto.SendMessageRequestDTO;
import tracz.messageservice.service.MessageService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody SendMessageRequestDTO requestDTO, @AuthenticationPrincipal Jwt jwt) {
        UUID senderId = UUID.fromString(jwt.getSubject());
        MessageDTO savedMessage = messageService.sendMessage(requestDTO, senderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageDTO>> getConversationMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<MessageDTO> messages = messageService.getMessagesForConversation(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }
}