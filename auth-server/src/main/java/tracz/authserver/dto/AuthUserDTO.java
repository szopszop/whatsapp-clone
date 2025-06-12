package tracz.authserver.dto;

import java.util.Set;
import java.util.UUID;

public record AuthUserDTO (
        UUID id,
        String email,
        Set<RoleDTO> roles){}
