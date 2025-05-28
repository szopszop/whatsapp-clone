package tracz.authserver.mapper;

import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.entity.AuthUser;

public class AuthUserMapper {
    public static AuthUser dtoToAuthUser(AuthUserDTO authUser) {
        return AuthUser.builder()
                .email(authUser.getEmail())
                .roles(authUser.getRoles())
                .build();
    }

    public static AuthUserDTO authUserToDto(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .roles(authUser.getRoles())
                .build();
    }
}
