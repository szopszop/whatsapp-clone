package tracz.authserver.mapper;

import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.entity.AuthUser;
import java.util.stream.Collectors;

public class AuthUserMapper {
    public static AuthUser dtoToAuthUser(AuthUserDTO dto) {
        return AuthUser.builder()
                .email(dto.getEmail())
                .roles(dto.getRoles().stream()
                        .map(RoleMapper::dtoToRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static AuthUserDTO authUserToDto(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .roles(authUser.getRoles().stream()
                        .map(RoleMapper::roleToDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
