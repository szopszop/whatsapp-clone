package tracz.userservice.service;

import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import tracz.userservice.dto.UserDTO;

public interface UserService {
    UserDTO findById(UUID id);
    UserDTO findByEmail(String email);
    boolean existsByEmail(@Valid String email);
    Page<UserDTO> getUsers(String email, Integer pageNumber, Integer pageSize);
}
