package tracz.userservice.service;

import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import tracz.userservice.dto.*;

public interface UserService {
    UserResponseDTO findByAuthServerUserId(UUID id);
    UserResponseDTO findByEmail(String email);
    boolean existsByEmail(@Valid String email);
    Page<UserResponseDTO> searchUsers(String email, Integer pageNumber, Integer pageSize);

    UserResponseDTO createUser(UserCreationRequestDTO creationRequest);
    UserResponseDTO updateMyProfile(UUID authUserId, UserProfileUpdateDTO updateDTO);
    void updateUserStatus(UUID authUserId, UserStatusUpdateDTO statusUpdateDTO);

}
