package tracz.notificationservice.dto;

import java.util.UUID;

public record UserStatusUpdatedEvent(UUID authUserId, String status) {}

