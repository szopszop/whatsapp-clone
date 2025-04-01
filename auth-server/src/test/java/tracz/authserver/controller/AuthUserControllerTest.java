package tracz.authserver.controller;

import java.time.Instant;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.DuplicateResourceException;
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
import tracz.authserver.config.ApiPaths;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.service.AuthUserService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthUserController.class)
@ActiveProfiles("unit-test")
class AuthUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private AuthUserService authUserService;

    private AuthUserController authUserController;

    static final String EMAIL = "email";
    static final String TEST_EMAIL = "test@test.com";
    static final String TEST_PASSWORD = "PasswordPassword123!";
    static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));

    UUID userId;
    AuthUserDTO userDTO;
    RegisterRequest request;

    @Test
    void registerDuplicateEmailTest() throws Exception {
        when(authUserService.register(any(RegisterRequest.class)))
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
        when(authUserService.register(any(RegisterRequest.class)))
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

}