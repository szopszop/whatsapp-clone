package tracz.authserver.dto;

import java.util.Set;
import java.util.UUID;

public record UserProvisionRequestDTO (
        UUID authUserId,
        String email,
        Set<String> roles
) {}