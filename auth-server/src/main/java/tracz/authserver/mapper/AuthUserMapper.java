package tracz.authserver.mapper;

import org.mapstruct.Mapper;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.entity.AuthUser;

@Mapper
public interface AuthUserMapper {
    AuthUser dtoToAuthUser(AuthUserDTO authUser);
    AuthUserDTO authUserToDto(AuthUser authUser);
}
