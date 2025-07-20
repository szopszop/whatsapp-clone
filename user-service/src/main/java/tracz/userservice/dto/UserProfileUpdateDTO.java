package tracz.userservice.dto;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateDTO(@Size(max = 50) String firstName,
                                   @Size(max = 50) String lastName,
                                   @Size(max = 255) String profileImageUrl,
                                   @Size(max = 500) String about) {
}

