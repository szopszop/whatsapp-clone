package tracz.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tracz.userservice.config.validation.Email;
import tracz.userservice.config.validation.Password;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @Size(max = 100)
    private String email;
    @Password
    private String password;
}
