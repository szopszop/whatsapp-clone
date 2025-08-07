package tracz.gatewayserver.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tracz.gatewayserver.config.ExceptionMessages;
import tracz.gatewayserver.dto.ErrorDTO;

import java.time.Instant;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                         org.springframework.boot.autoconfigure.web.WebProperties.Resources resources,
                                         ApplicationContext applicationContext,
                                         ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        logger.error("Error occurred: {}", error.getMessage(), error);

        return switch (error) {
            case ResponseStatusException ex ->
                    buildErrorResponse(ex.getStatusCode().value(), ex.getReason(), request.path());
            case AuthenticationException authenticationException ->
                    buildErrorResponse(HttpStatus.UNAUTHORIZED.value(), ExceptionMessages.UNAUTHORIZED_MESSAGE,
                            request.path());
            case AccessDeniedException accessDeniedException ->
                    buildErrorResponse(HttpStatus.FORBIDDEN.value(), ExceptionMessages.FORBIDDEN_MESSAGE,
                            request.path());
            case RequestNotPermitted requestNotPermitted ->
                    buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), ExceptionMessages.LIMIT_EXCEEDED,
                            request.path());
            default -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ExceptionMessages.INTERNAL_ERROR,
                    request.path());
        };
    }

    private Mono<ServerResponse> buildErrorResponse(int status, String message, String path) {
        ErrorDTO errorDTO = new ErrorDTO(
                Instant.now(),
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message,
                path
        );

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorDTO));
    }
}
