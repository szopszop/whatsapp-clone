package tracz.userservice.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import tracz.userservice.entity.Role;
import java.util.UUID;

@Data
@Builder
public class UserDTO {

    private UUID id;
    @Email
    private String email;
    private Role role;
}
