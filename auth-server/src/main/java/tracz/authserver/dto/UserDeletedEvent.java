package tracz.authserver.dto;

import java.util.UUID;

public record UserDeletedEvent(UUID authUserId) {}

