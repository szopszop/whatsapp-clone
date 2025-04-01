package tracz.authserver.controller;

import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static tracz.authserver.controller.AuthUserControllerTest.TEST_EMAIL;
import static tracz.authserver.controller.AuthUserControllerTest.TEST_PASSWORD;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.AuthUserRepository;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
class AuthUserControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine").withReuse(true);

    @Autowired
    AuthUserController authUserController;

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    AuthUserMapper authUserMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        for (int i = 0; i < 10; i++) {
            AuthUser user = AuthUser.builder()
                    .email("useremail" + i + "@email.com")
                    .password("SecurePassword123!" + i)
                    .roles(new HashSet<>(List.of("ROLE_USER")))
                    .build();
            authUserRepository.save(user);
        }
        AuthUser admin = AuthUser.builder()
                .email("adminemail@email.com")
                .password("SecurePassword123!")
                .roles(new HashSet<>(List.of("ROLE_ADMIN")))
                .build();
        authUserRepository.save(admin);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

    }

    @Transactional
    @Test
    void shouldSaveUser() {
        RegisterRequest request = RegisterRequest.builder().email(TEST_EMAIL).password(TEST_PASSWORD).build();

        ResponseEntity<AuthUserDTO> responseEntity = authUserController.register(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[locationUUID.length - 1]);
        authUserRepository.findById(savedUUID)
                .ifPresent(savedUser -> assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL));
    }


}