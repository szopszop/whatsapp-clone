package tracz.userservice.controller;

import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static tracz.userservice.controller.UserControllerTest.TEST_EMAIL;
import static tracz.userservice.controller.UserControllerTest.TEST_PASSWORD;
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
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.Role;
import tracz.userservice.entity.User;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
class UserControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine").withReuse(true);

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        for (int i = 0; i < 10; i++) {
            User user = User.builder().email("useremail" + i + "@email.com").password("SercurePassword123!" + i).role(Role.USER).build();
            userRepository.save(user);
        }
        User admin = User.builder().email("adminemail@email.com").password("SercurePassword123!").role(Role.ADMIN).build();
        userRepository.save(admin);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

    }

    @Transactional
    @Test
    void shouldSaveUser() {
        RegisterRequest request = RegisterRequest.builder().email(TEST_EMAIL).password(TEST_PASSWORD).build();

        ResponseEntity<UserDTO> responseEntity = userController.register(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[locationUUID.length - 1]);
        userRepository.findById(savedUUID).ifPresent(savedUser -> assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL));
    }


}