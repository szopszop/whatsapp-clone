package tracz.authserver.service;

import tracz.authserver.dto.*;
import java.util.UUID;

public interface AuthUserService {
    AuthUserDTO register(RegisterRequest request);
    void deleteUser(UUID authUserId);
}
