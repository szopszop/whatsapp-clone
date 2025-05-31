package tracz.authserver.dto;

import lombok.*;
import java.util.Set;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    private String email;
    private Set<String> roles;
}
