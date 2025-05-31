package tracz.authserver.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tracz.authserver.dto.UserRegisterDTO;
import tracz.authserver.dto.UserResponseDTO;

@Component
public class UserFallback implements UserFeignClient{

    @Override
    public ResponseEntity<UserResponseDTO> register(UserRegisterDTO userRegisterDTO) {
        return null;
    }
}
