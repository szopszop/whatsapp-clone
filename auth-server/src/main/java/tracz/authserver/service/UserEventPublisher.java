package tracz.authserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tracz.authserver.config.RabbitMQConfig;
import tracz.authserver.dto.UserDeletedEvent;
import tracz.authserver.dto.UserRegisteredEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Publishing user registration event for authUserId: {}", event.authUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EVENTS_EXCHANGE,
                RabbitMQConfig.USER_REGISTERED_ROUTING_KEY,
                event
        );
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        log.info("Publishing user deletion event for authUserId: {}", event.authUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EVENTS_EXCHANGE,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                event
        );
    }
}
