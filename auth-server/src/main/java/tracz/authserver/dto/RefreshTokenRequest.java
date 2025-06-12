package tracz.authserver.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest (
    @NotEmpty
    String refreshToken
){}