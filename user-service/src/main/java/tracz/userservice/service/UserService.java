package tracz.userservice.service;

import java.util.UUID;
import jakarta.validation.Valid;
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;

public interface UserService {
    UserDTO register(RegisterRequest request);
    UserDTO findById(UUID id);
    UserDTO findByEmail(String email);
    boolean existsByEmail(@Valid String email);
}
