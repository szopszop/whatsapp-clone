package tracz.userservice.dto;

import java.util.Set;
import java.util.UUID;

public record UserRegisterEvent(UUID authUserId, String email, Set<String> roles) {}

