package tracz.authserver.mapper;

import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.entity.AuthUser;
import java.util.stream.Collectors;

public class AuthUserMapper {
    public static AuthUser dtoToAuthUser(AuthUserDTO dto) {
        return AuthUser.builder()
                .email(dto.email())
                .roles(dto.roles().stream()
                        .map(RoleMapper::dtoToRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static AuthUserDTO authUserToDto(AuthUser authUser) {
        return new AuthUserDTO (
                authUser.getId(),
                authUser.getEmail(),
                authUser.getRoles().stream()
                        .map(RoleMapper::roleToDto)
                        .collect(Collectors.toSet())
        );
    }
}
