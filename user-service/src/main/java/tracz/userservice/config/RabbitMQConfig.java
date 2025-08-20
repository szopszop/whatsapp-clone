package tracz.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENTS_EXCHANGE = "user_events_exchange";

    public static final String USER_REGISTRATION_QUEUE = "q.user.registration";
    public static final String USER_DELETION_QUEUE = "q.user.deletion";
    public static final String USER_STATUS_UPDATE_QUEUE = "q.user.status_update.notifications";

    public static final String USER_REGISTERED_ROUTING_KEY = "user.event.registered";
    public static final String USER_DELETED_ROUTING_KEY = "user.event.deleted";
    public static final String USER_STATUS_UPDATED_ROUTING_KEY = "user.event.status.updated";

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue userRegistrationQueue() {
        return new Queue(USER_REGISTRATION_QUEUE);
    }

    @Bean
    public Queue userDeletionQueue() {
        return new Queue(USER_DELETION_QUEUE);
    }

    @Bean
    public Queue userStatusUpdateQueue() {
        return new Queue(USER_STATUS_UPDATE_QUEUE);
    }

    @Bean
    public Binding userRegistrationBinding(Queue userRegistrationQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userRegistrationQueue).to(userEventsExchange).with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding userDeletionBinding(Queue userDeletionQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userDeletionQueue).to(userEventsExchange).with(USER_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding userStatusUpdateBinding(Queue userStatusUpdateQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userStatusUpdateQueue).to(userEventsExchange).with("user.event.status.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}