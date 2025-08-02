package tracz.notificationservice.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tracz.notificationservice.dto.MessageNotificationEvent;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseMessaging firebaseMessaging;
    private final WebClient.Builder webClientBuilder;

    @RabbitListener(queues = "notification.queue")
    public void handleMessage(MessageNotificationEvent event) {
        log.info("Received notification event for user: {}", event.recipientId());

        messagingTemplate.convertAndSendToUser(
                event.recipientId().toString(),
                "/queue/messages",
                event
        );
        log.info("Sent notification via WebSocket to user {}", event.recipientId());


        Set<String> fcmTokens = getFcmTokensForUser(event.recipientId());

        if (fcmTokens == null || fcmTokens.isEmpty()) {
            log.warn("No FCM tokens found for user {}", event.recipientId());
            return;
        }

        Notification notification = Notification.builder()
                .setTitle("Nowa wiadomość")
                .setBody(event.content())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("messageId", event.messageId())
                .build();

        try {
            firebaseMessaging.sendEachForMulticast(message);
            log.info("Successfully sent push notification to {} devices for user {}", fcmTokens.size(), event.recipientId());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification for user {}", event.recipientId(), e);
        }
    }

    private Set<String> getFcmTokensForUser(UUID userId) {
        return webClientBuilder.build().get()
                .uri("http://USER-SERVICE/internal/api/v1/users/{userId}/fcm-tokens", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {})
                .block();
    }
}