package tracz.userservice.mapper;

import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.User;

public class UserMapper {

    public static User dtoToUser(UserDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }


    public static UserDTO userToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
