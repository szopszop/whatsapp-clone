package tracz.notificationservice.service;

import com.google.firebase.messaging.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tracz.notificationservice.dto.MessageNotificationEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private MessageListener messageListener;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        messageListener = new MessageListener(messagingTemplate, webClientBuilder, firebaseMessaging);
    }

    @Test
    void handleMessage_shouldSendWebSocketNotification() {
        // Given
        UUID recipientId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        String messageId = "msg-123";
        String content = "Hello, world!";
        Instant createdAt = Instant.now();

        MessageNotificationEvent event = new MessageNotificationEvent(
                messageId, conversationId, senderId, recipientId, content, createdAt
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(Collections.emptySet()));

        // When
        messageListener.handleMessage(event);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                event
        );
        verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    void handleMessage_shouldSendPushNotification_whenFcmTokensExist() throws FirebaseMessagingException {
        // Given
        UUID recipientId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        String messageId = "msg-123";
        String content = "Hello, world!";
        Instant createdAt = Instant.now();
        Set<String> fcmTokens = Set.of("token1", "token2");

        MessageNotificationEvent event = new MessageNotificationEvent(
                messageId, conversationId, senderId, recipientId, content, createdAt
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(fcmTokens));

        BatchResponse batchResponse = mock(BatchResponse.class);
        when(batchResponse.getSuccessCount()).thenReturn(2);
        when(batchResponse.getFailureCount()).thenReturn(0);
        when(firebaseMessaging.sendMulticast(any(MulticastMessage.class))).thenReturn(batchResponse);

        // When
        messageListener.handleMessage(event);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                event
        );
        verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
        verify(firebaseMessaging).sendMulticast(any(MulticastMessage.class));
    }

    @Test
    void handleMessage_shouldNotSendPushNotification_whenNoFcmTokens() throws FirebaseMessagingException {
        // Given
        UUID recipientId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        String messageId = "msg-123";
        String content = "Hello, world!";
        Instant createdAt = Instant.now();

        MessageNotificationEvent event = new MessageNotificationEvent(
                messageId, conversationId, senderId, recipientId, content, createdAt
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(Collections.emptySet()));

        // When
        messageListener.handleMessage(event);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                event
        );
        verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
        verify(firebaseMessaging, never()).sendMulticast(any(MulticastMessage.class));
    }

    @Test
    void handleMessage_shouldHandleErrorWhenFetchingFcmTokens() throws FirebaseMessagingException {
        // Given
        UUID recipientId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        String messageId = "msg-123";
        String content = "Hello, world!";
        Instant createdAt = Instant.now();

        MessageNotificationEvent event = new MessageNotificationEvent(
                messageId, conversationId, senderId, recipientId, content, createdAt
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("Error fetching FCM tokens")));

        // When
        messageListener.handleMessage(event);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                event
        );
        verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
        verify(firebaseMessaging, never()).sendMulticast(any(MulticastMessage.class));
    }
}
