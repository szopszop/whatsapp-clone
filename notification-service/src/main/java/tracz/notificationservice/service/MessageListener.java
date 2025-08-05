package tracz.notificationservice.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tracz.notificationservice.dto.MessageNotificationEvent;

import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseMessaging firebaseMessaging;
    private final WebClient webClient;

    // Constructor to build the WebClient once for reuse
    public MessageListener(SimpMessagingTemplate messagingTemplate,
                           FirebaseMessaging firebaseMessaging,
                           WebClient.Builder webClientBuilder) {
        this.messagingTemplate = messagingTemplate;
        this.firebaseMessaging = firebaseMessaging;
        // It's a best practice to build the client once and set the base URL
        this.webClient = webClientBuilder.baseUrl("http://USER-SERVICE").build();
    }

    @RabbitListener(queues = "notification.queue")
    public void handleMessage(MessageNotificationEvent event) {
        log.info("Received notification event for user: {}", event.recipientId());

        // 1. Send WebSocket notification (this is synchronous and can remain so)
        sendWebSocketNotification(event);

        // 2. Asynchronously fetch FCM tokens and then send push notifications
        getFcmTokensForUser(event.recipientId())
                .subscribe(
                        fcmTokens -> {
                            if (fcmTokens == null || fcmTokens.isEmpty()) {
                                log.warn("No FCM tokens found for user {}", event.recipientId());
                                return; // No tokens, so we are done.
                            }
                            sendPushNotification(event, fcmTokens);
                        },
                        error -> log.error("Failed to retrieve FCM tokens for user {}", event.recipientId(), error)
                );
    }

    private void sendWebSocketNotification(MessageNotificationEvent event) {
        messagingTemplate.convertAndSendToUser(
                event.recipientId().toString(),
                "/queue/messages",
                event
        );
        log.info("Sent notification via WebSocket to user {}", event.recipientId());
    }

    private void sendPushNotification(MessageNotificationEvent event, Set<String> fcmTokens) {
        Notification notification = Notification.builder()
                .setTitle("Nowa wiadomość") // Consider externalizing this string to a config file
                .setBody(event.content())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("messageId", event.messageId())
                .build();

        try {
            // Note: The Firebase SDK call here is blocking. For full reactivity,
            // you would use its async methods, but this is a significant improvement.
            firebaseMessaging.sendEachForMulticast(message);
            log.info("Successfully sent push notification to {} devices for user {}", fcmTokens.size(), event.recipientId());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification for user {}", event.recipientId(), e);
        }
    }

    private Mono<Set<String>> getFcmTokensForUser(UUID userId) {
        // Return a Mono to allow for non-blocking execution
        return this.webClient.get()
                .uri("/internal/api/v1/users/{userId}/fcm-tokens", userId)
                .retrieve()
                // FIX: Explicitly define the generic type for ParameterizedTypeReference
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {})
                // In case of an error from user-service, resume with an empty Mono
                // to prevent breaking the chain and allow other notifications to proceed.
                .onErrorResume(e -> {
                    log.error("Error fetching FCM tokens for user {}", userId, e);
                    return Mono.empty();
                });
    }
}