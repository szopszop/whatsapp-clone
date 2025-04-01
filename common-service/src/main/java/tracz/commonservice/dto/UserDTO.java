package tracz.commonservice.dto;

import java.util.UUID;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    @Email
    private String email;
    private String role;
}
