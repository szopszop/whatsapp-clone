package tracz.userservice.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tracz.userservice.config.ExceptionMessages;
import tracz.userservice.dto.ErrorDTO;
import tracz.userservice.exception.TooManyRequestsException;
import tracz.userservice.exception.UserAlreadyExistsException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorDTO ErrorDTO = new ErrorDTO(
                Instant.now(),
                status.value(),
                ExceptionMessages.VALIDATION_FAILED,
                "Input validation errors",
                request.getDescription(false).replace("uri=", ""),
                errors
        );
        log.error("Validation failed for request {}: {}", request.getDescription(false), errors);
        return new ResponseEntity<>(ErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        log.warn("User registration conflict: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.BAD_REQUEST,
                "Constraint Violation: " + ex.getConstraintViolations(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        log.warn("Response status exception: {} - {}", ex.getStatusCode(), ex.getReason());
        return buildErrorDTOResponseEntity((HttpStatus) ex.getStatusCode(), ex.getReason(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failure: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.UNAUTHORIZED, ExceptionMessages.UNAUTHORIZED_MESSAGE, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.FORBIDDEN, ExceptionMessages.FORBIDDEN_MESSAGE, request);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorDTO> handleTooManyRequestsException(TooManyRequestsException ex, WebRequest request) {
        log.warn("Too many requests: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorDTO> handleRequestNotPermitted(RequestNotPermitted ex, WebRequest request) {
        log.error("Rate limit exceeded: {}", ex.getMessage());
        return buildErrorDTOResponseEntity(HttpStatus.TOO_MANY_REQUESTS, ExceptionMessages.LIMIT_EXCEEDED, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorDTOResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionMessages.INTERNAL_ERROR, request);
    }



    private ResponseEntity<ErrorDTO> buildErrorDTOResponseEntity(HttpStatus status, String message, WebRequest request) {
        ErrorDTO ErrorDTO = new ErrorDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(ErrorDTO, status);
    }
}