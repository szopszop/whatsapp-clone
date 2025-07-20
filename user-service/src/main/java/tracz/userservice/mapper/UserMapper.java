package tracz.userservice.mapper;

import tracz.userservice.dto.*;
import tracz.userservice.entity.User;
import java.util.Collections;

public class UserMapper {

    public static User dtoToUser(UserCreationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .authServerUserId(dto.authUserId())
                .email(dto.email())
                .roles(dto.roles() != null ? dto.roles() : Collections.emptySet())
                .build();
    }

    public static UserResponseDTO userToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
                user.getId(),
                user.getAuthServerUserId(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureUrl(),
                user.getAbout()
        );
    }

    public static void updateFromDto(UserProfileUpdateDTO updateDTO, User userToUpdate) {
        if (updateDTO == null) {
            return;
        }
        if (updateDTO.firstName() != null) {
            userToUpdate.setFirstName(updateDTO.firstName());
        }
        if (updateDTO.lastName() != null) {
            userToUpdate.setLastName(updateDTO.lastName());
        }
        if (updateDTO.profileImageUrl() != null) {
            userToUpdate.setProfilePictureUrl(updateDTO.profileImageUrl());
        }
        if (updateDTO.about() != null) {
            userToUpdate.setAbout(updateDTO.about());
        }
    }
}
