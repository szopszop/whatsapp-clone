package tracz.authserver.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
public class RefreshTokenRequest {
    @NotEmpty
    String refreshToken;

}