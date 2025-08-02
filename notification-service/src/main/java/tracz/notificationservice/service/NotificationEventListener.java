package tracz.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tracz.notificationservice.config.RabbitMQConfig;
import tracz.notificationservice.dto.UserStatusUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    @RabbitListener(queues = RabbitMQConfig.USER_STATUS_UPDATE_QUEUE)
    public void handleUserStatusUpdate(UserStatusUpdatedEvent event) {
        log.info("NOTIFICATION-SERVICE: User {} status changed to {}. Preparing notification...",
                event.authUserId(), event.status());
    }
}
