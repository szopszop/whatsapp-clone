package tracz.userservice.dto;

import java.util.UUID;

public record UserStatusUpdatedEvent(UUID authUserId, String status) {}

