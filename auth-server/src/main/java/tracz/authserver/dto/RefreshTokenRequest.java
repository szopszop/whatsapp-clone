package tracz.authserver.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}