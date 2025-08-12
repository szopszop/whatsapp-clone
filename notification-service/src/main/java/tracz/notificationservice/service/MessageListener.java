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
    private final WebClient webClient;
    private final FirebaseMessaging firebaseMessaging;

    public MessageListener(SimpMessagingTemplate messagingTemplate,
                           WebClient.Builder webClientBuilder,
                           FirebaseMessaging firebaseMessaging) {
        this.messagingTemplate = messagingTemplate;
        this.webClient = webClientBuilder.baseUrl("http://USER-SERVICE").build();
        this.firebaseMessaging = firebaseMessaging;
    }

    @RabbitListener(queues = "notification.queue")
    public void handleMessage(MessageNotificationEvent event) {
        log.info("Received notification event for user: {}", event.recipientId());

        sendWebSocketNotification(event);

        getFcmTokensForUser(event.recipientId())
                .subscribe(
                        fcmTokens -> {
                            if (fcmTokens == null || fcmTokens.isEmpty()) {
                                log.warn("No FCM tokens found for user {}", event.recipientId());
                                return;
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
        try {
            Notification notification = Notification.builder()
                    .setTitle("Nowa wiadomość") // Consider externalizing this string to a config file
                    .setBody(event.content())
                    .build();

            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(fcmTokens)
                    .setNotification(notification)
                    .putData("messageId", event.messageId())
                    .putData("senderId", event.senderId().toString())
                    .putData("recipientId", event.recipientId().toString())
                    .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();

            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("Successfully sent message to {} devices. Failed: {}", 
                    response.getSuccessCount(), response.getFailureCount());

            if (response.getFailureCount() > 0) {
                for (int i = 0; i < response.getResponses().size(); i++) {
                    if (!response.getResponses().get(i).isSuccessful()) {
                        log.warn("Failed to send message to token {}: {}", 
                                fcmTokens.toArray()[i], 
                                response.getResponses().get(i).getException().getMessage());
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification", e);
        }
    }

    private Mono<Set<String>> getFcmTokensForUser(UUID userId) {
        return this.webClient.get()
                .uri("/internal/api/v1/users/{userId}/fcm-tokens", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {})

                .onErrorResume(e -> {
                    log.error("Error fetching FCM tokens for user {}", userId, e);
                    return Mono.empty();
                });
    }
}
