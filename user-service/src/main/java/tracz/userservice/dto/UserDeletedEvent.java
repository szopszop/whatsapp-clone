package tracz.userservice.dto;

import java.util.UUID;

public record UserDeletedEvent(UUID authUserId) {}

