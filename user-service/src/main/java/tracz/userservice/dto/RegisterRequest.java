package tracz.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Email
    @Size(min = 5, max = 100)
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
