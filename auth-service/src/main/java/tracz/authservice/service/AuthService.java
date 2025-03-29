package tracz.authservice.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import tracz.authservice.dto.*;

public interface AuthService {
    ResponseEntity<AuthResponse> register(RegisterRequest email);
    AuthResponse refreshToken(RefreshTokenRequest request);
    AuthResponse login(@Valid LoginRequest request);
}
