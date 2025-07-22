package tracz.userservice.dto;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateDTO(@NotNull String status) {
}
