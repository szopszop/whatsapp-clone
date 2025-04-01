package tracz.authserver.dto;

import lombok.*;
import tracz.authserver.config.validation.Email;
import tracz.authserver.config.validation.Password;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Email
    String email;
    @Password
    String password;
}
