package tracz.authserver.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import tracz.authserver.config.validation.Email;
import tracz.authserver.config.validation.Password;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Email
    @Size(max = 100)
    private String email;
    @Password
    private String password;
}
