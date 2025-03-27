package tracz.userservice.controller;

import java.time.Instant;
import java.util.UUID;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.config.ExceptionMessages;
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.entity.Role;
import tracz.userservice.service.UserService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@ActiveProfiles("unit-test")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserController userController;

    static final String EMAIL = "email";
    static final String TEST_EMAIL = "test@test.com";
    static final String TEST_PASSWORD = "SecurePassword123!";
    static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));

    UUID userId;
    UserDTO userDTO;
    RegisterRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userDTO = UserDTO.builder()
                .id(userId)
                .email(TEST_EMAIL)
                .role(Role.USER)
                .build();
        request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);
        userController = new UserController(userService);
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.findById(userId)).thenReturn(userDTO);

        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, userId)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.id", is(userId.toString())));
    }

    @Test
    void getUserByEmailTest() throws Exception {
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(userDTO);

        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
                        .with(jwtRequestPostProcessor)
                        .queryParam(EMAIL, TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void getUserByIdNotFoundTest() throws Exception {
        when(userService.findById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.USER_NOT_FOUND))
                .andExpect(jsonPath("$.error").value(ExceptionMessages.NOT_FOUND))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getUserByEmailNotFoundTest() throws Exception {
        when(userService.findByEmail(any(String.class)))
                .thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
                        .with(jwtRequestPostProcessor)
                        .queryParam(EMAIL, TEST_EMAIL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.USER_NOT_FOUND))
                .andExpect(jsonPath("$.error").value(ExceptionMessages.NOT_FOUND))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getUserByIdUnauthenticatedTest() throws Exception {
        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, userId))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\""));

    }

    @Test
    void getUserByEmailUnauthenticatedTest() throws Exception {
        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
                        .queryParam(EMAIL, TEST_EMAIL))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\""));
    }

    @Test
    void registerDuplicateEmailTest() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException(ExceptionMessages.EMAIL_EXISTS));

        mockMvc.perform(post(ApiPaths.USER_API)
                        .with(jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value(ExceptionMessages.CONFLICT))
                .andExpect(jsonPath("$.message").value(ExceptionMessages.EMAIL_EXISTS));
    }

    @Test
    void registerUserTest() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(userDTO);

        mockMvc.perform(post(ApiPaths.USER_API)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }


    @Test
    void registerUserWithInvalidEmailTest() throws Exception {
        String[] invalidEmails = {"invalid-email", "invalid@", "in@com", "in.@com", "i@a", "@i.com",
                "invalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalid@invalid.com"};

        for (String email : invalidEmails) {
            RegisterRequest request = RegisterRequest.builder()
                    .email(email)
                    .password(TEST_PASSWORD)
                    .build();

            mockMvc.perform(post(ApiPaths.USER_API)
                            .with(jwtRequestPostProcessor)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }

    @Test
    void testExistsByEmail() throws Exception {
        when(userService.existsByEmail(any(String.class))).thenReturn(true);
        mockMvc.perform(get(ApiPaths.USER_API_EXISTS_BY_EMAIL)
                        .queryParam(EMAIL, TEST_EMAIL)
                        .with(jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}