package tracz.authserver.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest (
    @Email
    @Size(max = 100)
    String email,
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,100}$",
            message = "Password must be at least 8 characters long, with 1 uppercase letter, "
                    + "with 1 lower case letter, with 1 special character")
    @Size(max = 50)
    String password) {}

