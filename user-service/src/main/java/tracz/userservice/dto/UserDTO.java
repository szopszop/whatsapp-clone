package tracz.userservice.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tracz.userservice.entity.Role;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    @Email
    private String email;
    private Role role;
}
