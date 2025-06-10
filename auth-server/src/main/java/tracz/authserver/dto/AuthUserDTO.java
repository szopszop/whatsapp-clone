package tracz.authserver.dto;

import lombok.*;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private UUID id;
    private String email;
    private Set<RoleDTO> roles;
}
