package tracz.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterRequest {

    @Size(max = 100)
    private String email;
    private String password;
}
