package tracz.authserver.mapper;

import tracz.authserver.dto.RoleDTO;
import tracz.authserver.entity.Role;

public class RoleMapper {
    public static RoleDTO roleToDto(Role role) {
        return new RoleDTO (role.getId(),role.getName());
    }

    public static Role dtoToRole(RoleDTO dto) {
        return Role.builder()
                .name(dto.name())
                .build();

    }
}

