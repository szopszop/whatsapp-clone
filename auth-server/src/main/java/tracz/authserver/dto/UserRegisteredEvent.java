package tracz.authserver.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record UserRegisteredEvent(UUID authUserId, String email, Set<String> roles) implements Serializable {}

