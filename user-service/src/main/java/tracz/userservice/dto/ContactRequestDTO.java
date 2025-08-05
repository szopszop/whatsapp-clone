package tracz.userservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContactRequestDTO(
    @NotNull
    UUID contactId
) {}