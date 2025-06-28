package tracz.authserver.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tracz.authserver.dto.UserProvisionRequestDTO;

@FeignClient(name = "USER-SERVICE", fallback = UserServiceFallback.class)
public interface UserServiceFeignClient {

    @PostMapping("/internal/api/v1/users")
    @CircuitBreaker(name = "userServiceProvision", fallbackMethod = "provisionUserFallback")
    ResponseEntity<Void> provisionUser(@RequestBody UserProvisionRequestDTO provisionRequest);
}

