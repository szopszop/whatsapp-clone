package tracz.authserver.service;

import tracz.authserver.dto.*;

public interface AuthUserService {
    AuthUserDTO register(RegisterRequest request);
}
