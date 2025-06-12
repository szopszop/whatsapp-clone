package tracz.authserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Email
        String email,
        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
