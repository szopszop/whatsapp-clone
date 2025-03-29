package tracz.authservice.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import tracz.commonservice.config.validation.Email;
import tracz.commonservice.config.validation.Password;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @Size(max = 100)
    private String email;
    @Password
    private String password;
    @Password
    private String confirmPassword;

}
