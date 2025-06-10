package tracz.authserver.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tracz.authserver.dto.UserProvisionRequestDTO;

// "user-service" should match the spring.application.name of your user microservice
// as registered with Eureka or the direct URL if not using service discovery.
@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
public interface UserServiceFeignClient {

    // Define the endpoint in your user-service that creates/provisions a user
    // Example: POST /internal/api/v1/users
    @PostMapping("/internal/api/v1/users") // Adjust this path to your actual user-service endpoint
    @CircuitBreaker(name = "userServiceProvision", fallbackMethod = "provisionUserFallback")
    ResponseEntity<Void> provisionUser(@RequestBody UserProvisionRequestDTO provisionRequest);

    // Default fallback method within the interface (optional, if not using a separate fallback class for this specific method)
    // This is an alternative to the fallbackMethod in @CircuitBreaker if UserServiceFallback handles all methods.
    // default ResponseEntity<Void> provisionUserFallback(UserProvisionRequestDto provisionRequest, Throwable t) {
    //     log.error("Fallback: Error provisioning user {} in user-service via Feign: {}", provisionRequest.email(), t.getMessage());
    //     // Depending on requirements, you might return a specific status or rethrow a custom exception
    //     return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    // }
}

