package tracz.authserver.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest (
    @Email
    @NotBlank
    @Size(max = 100)
    String email,
    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 characters long, with 1 uppercase letter, "
                    + "with 1 lower case letter, with 1 special character")
    @Size(max = 50)
    String password) {}

