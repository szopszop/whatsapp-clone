package tracz.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tracz.userservice.config.RabbitMQConfig;
import tracz.userservice.dto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserStatusUpdated(UserStatusUpdatedEvent event) {
        log.info("Publishing user status update event for {}: {}", event.authUserId(), event.status());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EVENTS_EXCHANGE,
                RabbitMQConfig.USER_STATUS_UPDATED_ROUTING_KEY,
                event
        );
    }
}