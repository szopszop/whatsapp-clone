package tracz.authserver.service;

import tracz.authserver.dto.*;

public interface AuthUserService {
    AuthUserDTO register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest token);
}
