package tracz.authserver.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Email
    String email;
    @NotBlank(message = "Password cannot be blank")
    String password;
}
