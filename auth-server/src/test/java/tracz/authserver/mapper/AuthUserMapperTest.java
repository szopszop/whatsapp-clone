package tracz.authserver.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.entity.AuthUser;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static tracz.authserver.controller.AuthUserControllerTest.TEST_EMAIL;
import static tracz.authserver.controller.AuthUserControllerTest.TEST_PASSWORD;

class AuthUserMapperTest {

    private final AuthUserMapper authUserMapper = Mappers.getMapper(AuthUserMapper.class);

    @Test
    void dtoToAuthUserTest() {
        AuthUserDTO dto = AuthUserDTO.builder()
                .id(UUID.randomUUID())
                .email(TEST_EMAIL)
                .roles(new HashSet<>(List.of("ROLE_USER")))
                .build();

        AuthUser authUser = authUserMapper.dtoToAuthUser(dto);

        assertThat(authUser).isNotNull();
        assertThat(authUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(authUser.getRoles()).isEqualTo(Set.of("ROLE_USER"));
        assertThat(authUser.getPassword()).isNull();
    }

    @Test
    void authUserToDto() {
        UUID id = UUID.randomUUID();
        AuthUser authUser = AuthUser.builder()
                .id(id)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(new HashSet<>(List.of("ROLE_USER")))
                .build();

        AuthUserDTO dto = authUserMapper.authUserToDto(authUser);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(dto.getRoles()).containsExactly("ROLE_USER");
        assertThat(dto).hasNoNullFieldsOrPropertiesExcept("password");
    }
}