package tracz.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tracz.userservice.config.RabbitMQConfig;
import tracz.userservice.dto.*;
import tracz.userservice.dto.UserRegisterEvent;
import tracz.userservice.exception.UserAlreadyExistsException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegisterEvent event) {
        log.info("Received user registration event for email: {}", event.email());
        UserCreationRequestDTO creationRequest = new UserCreationRequestDTO(
                event.authUserId(), event.email(), event.roles()
        );
        try {
            userService.createUser(creationRequest);
        } catch (UserAlreadyExistsException e) {
            log.warn("User from registration event already exists. Ignoring. Details: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing user registration event for {}.", event.email(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process user registration", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_DELETION_QUEUE)
    public void handleUserDeletion(UserDeletedEvent event) {
        log.info("Received user deletion event for authUserId: {}", event.authUserId());
        try {
            userService.deleteUserByAuthId(event.authUserId());
        } catch (Exception e) {
            log.error("Error processing user deletion event for authUserId {}.", event.authUserId(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process user deletion", e);
        }
    }
}
