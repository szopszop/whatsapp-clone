package tracz.userservice.service;

import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import tracz.userservice.dto.UserCreationRequestDTO;
import tracz.userservice.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO findById(UUID id);
    UserResponseDTO findByEmail(String email);
    boolean existsByEmail(@Valid String email);
    Page<UserResponseDTO> getUsers(String email, Integer pageNumber, Integer pageSize);

    UserResponseDTO createUser(UserCreationRequestDTO creationRequest);
}
