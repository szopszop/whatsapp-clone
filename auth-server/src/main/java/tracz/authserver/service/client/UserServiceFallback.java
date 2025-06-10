package tracz.authserver.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tracz.authserver.dto.UserProvisionRequestDTO;

// --- Fallback Implementation ---
@Component
@Slf4j
class UserServiceFallback implements UserServiceFeignClient {

    @Override
    public ResponseEntity<Void> provisionUser(UserProvisionRequestDTO provisionRequest) {
        // This is the class-level fallback method.
        // The @CircuitBreaker's fallbackMethod attribute takes precedence if specified for a method.
        log.error("UserServiceFallback: Error provisioning user {} in user-service. Service might be down or request failed.", provisionRequest.email());
        // Here, you can implement logic like:
        // 1. Logging the failure.
        // 2. Queueing the request for later processing (e.g., using a message queue or a DB table).
        // 3. Returning a specific error response or a default value.
        // For this example, we'll log and return a 503 Service Unavailable.
        // You might want to throw a custom exception that can be handled by GlobalExceptionHandler
        // to provide a more structured error response to the original caller.
        // For instance: throw new ServiceUnavailableException("User service is currently unavailable. User provisioning failed for " + provisionRequest.email());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    // This method is referenced by @CircuitBreaker's fallbackMethod if you choose that approach,
    // It needs to have the same signature as the Feign client method, plus a Throwable parameter.
    public ResponseEntity<Void> provisionUserFallback(UserProvisionRequestDTO provisionRequest, Throwable t) {
        log.error("CircuitBreaker Fallback: Error provisioning user {} in user-service via Feign: {}. Exception: {}",
                provisionRequest.email(), t.getMessage(), t.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
