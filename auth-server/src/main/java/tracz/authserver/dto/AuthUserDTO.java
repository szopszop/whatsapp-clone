package tracz.authserver.dto;

import java.util.Set;
import java.util.UUID;
import lombok.*;
import tracz.authserver.config.validation.Email;
import tracz.authserver.config.validation.Password;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private UUID id;

    @Email
    private String email;
    private Set<String> roles;
}
