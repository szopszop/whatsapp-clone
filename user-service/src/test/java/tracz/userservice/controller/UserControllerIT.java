package tracz.userservice.controller;

import java.util.UUID;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tracz.userservice.entity.Role;
import tracz.userservice.entity.User;
import tracz.userservice.mapper.UserMapper;
import tracz.userservice.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("integration-test")
class UserControllerIT {

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
    void setUp() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    void shouldSaveUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("email43432@email.com")
                .password("p1asfwqswor32131@!#d")
                .role(Role.USER)
                .build();
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
    }
}