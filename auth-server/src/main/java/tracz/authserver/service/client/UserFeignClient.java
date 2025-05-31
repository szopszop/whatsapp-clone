package tracz.authserver.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tracz.authserver.dto.UserRegisterDTO;
import tracz.authserver.dto.UserResponseDTO;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PostMapping(value = "/api/v1/user/register", consumes = "application/json")
    ResponseEntity<UserResponseDTO>register(@RequestBody UserRegisterDTO userRegisterDTO);

}
