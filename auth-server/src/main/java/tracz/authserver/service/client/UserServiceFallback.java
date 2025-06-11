package tracz.authserver.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tracz.authserver.dto.UserProvisionRequestDTO;
import tracz.authserver.exception.ServiceUnavailableException;

@Component
@Slf4j
class UserServiceFallback implements FallbackFactory<UserServiceFeignClient> {

    @Override
    public UserServiceFeignClient create(Throwable cause) {
        return provisionRequest -> {
            log.error("Fallback for user provisioning of email: {}. Reason: {}",
                    provisionRequest.email(), cause.getMessage());
            throw new ServiceUnavailableException("User provisioning failed in user-service.", cause);
        };
    }
}
