package tracz.userservice.dto;

import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

public record UserCreationRequestDTO(
        @NotNull UUID authUserId,
        @NotEmpty @Email String email,
        Set<String> roles
) {}
