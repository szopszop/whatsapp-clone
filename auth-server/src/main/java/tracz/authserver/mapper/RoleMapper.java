package tracz.authserver.mapper;

import tracz.authserver.dto.RoleDTO;
import tracz.authserver.entity.Role;

public class RoleMapper {
    public static RoleDTO roleToDto(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static Role dtoToRole(RoleDTO dto) {
        return Role.builder()
                .name(dto.getName())
                .build();

    }
}

