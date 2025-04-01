package tracz.userservice.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
            User user = User.builder()
                    .email("useremail" + i + "@email.com")
                    .password("SercurePassword123!" + i)
                    .role(Role.USER).build();
            userRepository.save(user);
        }
        User admin = User.builder()
                .email("adminemail@email.com")
                .password("SercurePassword123!")
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

    }


}