package tracz.authservice.dto;

import lombok.Data;
import tracz.commonservice.config.validation.Email;
import tracz.commonservice.config.validation.Password;

@Data
public class LoginRequest {
    @Email
    String email;
    @Password
    String password;
}
