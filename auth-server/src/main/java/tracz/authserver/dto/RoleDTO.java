package tracz.authserver.dto;

import java.util.UUID;

public record RoleDTO(
        UUID id,
        String name
) { }
