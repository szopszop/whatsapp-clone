package tracz.authserver.dto;

import java.util.UUID;

public record UserStatusUpdatedEvent(UUID authUserId, String status) {
}
