package tracz.authserver.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken
){}