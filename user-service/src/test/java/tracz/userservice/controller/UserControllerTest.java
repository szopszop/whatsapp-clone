package tracz.userservice.controller;

import java.time.Instant;
import java.util.UUID;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
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

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@test.com";
    private final UserDTO userDTO = new UserDTO(userId, email, Role.USER);

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));


    @Test
    void getUserByIdTest() throws Exception {
        when(userService.findById(userId)).thenReturn(userDTO);

        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, userId)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id", is(userId.toString())));
    }

    @Test
    void getUserByEmailTest() throws Exception {
        when(userService.findByEmail(email)).thenReturn(userDTO);

        mockMvc.perform(get(ApiPaths.USER_API + "/by-email")
                        .with(jwtRequestPostProcessor)
                        .queryParam("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void getUserByEmailNotFoundTest() throws Exception {
        when(userService.findByEmail(any(String.class))).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(ApiPaths.USER_API + "/by-email")
                        .with(jwtRequestPostProcessor)
                        .queryParam("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"));

    }

    @Test
    void getUserByIdNotFoundTest() throws Exception {
        when(userService.findById(any(UUID.class))).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"));
    }

    @Test
    void registerDuplicateEmailTest() throws Exception {
        RegisterRequest request = new RegisterRequest(email, "SecurePassword123!");
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Email already exists"));

        mockMvc.perform(post(ApiPaths.USER_API)
                        .with(jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email already exists"));


    }
}