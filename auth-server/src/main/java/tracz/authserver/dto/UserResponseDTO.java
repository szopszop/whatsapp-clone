package tracz.authserver.dto;

import lombok.*;
import tracz.authserver.config.validation.Email;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String email;
}
