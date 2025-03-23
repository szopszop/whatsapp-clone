package tracz.userservice.mapper;

import org.mapstruct.Mapper;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.User;

@Mapper
public interface UserMapper {
    User dtoToUser(UserDTO dto);
    UserDTO userToDto(User user);
}
