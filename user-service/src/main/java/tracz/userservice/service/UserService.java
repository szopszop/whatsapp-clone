package tracz.userservice.service;

import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;

public interface UserService {
    UserDTO register(RegisterRequest request);
    UserDTO findById(UUID id);
    UserDTO findByEmail(String email);
    boolean existsByEmail(@Valid String email);
    Page<UserDTO> getUsers(String email, Integer pageNumber, Integer pageSize);
}
