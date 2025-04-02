package tracz.authserver.controller;

import java.time.Instant;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tracz.authserver.config.*;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.service.AuthUserService;

@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthUserController.class)
@ActiveProfiles("unit-test")
class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private RegisteredClientRepository registeredClientRepository;

    @MockitoBean
    private AuthUserService authUserService;

    private AuthUserController authUserController;

    static final String EMAIL = "email";
    static final String TEST_EMAIL = "test@test.com";
    static final String TEST_PASSWORD = "PasswordPassword123!";
    static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));

    UUID authUserId;
    AuthUserDTO authUserDTO;
    RegisterRequest request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        authUserId = UUID.randomUUID();
        authUserDTO = AuthUserDTO.builder()
                .id(authUserId)
                .email(TEST_EMAIL)
                .roles(new HashSet<>(List.of("ROLE_USER")))
                .build();
        request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);
        authUserController = new AuthUserController(authUserService);
    }

    @Test
    void registerUserTest() throws Exception {
        when(authUserService.register(any(RegisterRequest.class)))
                .thenReturn(authUserDTO);

        mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(authUserId.toString()))
                .andExpect(jsonPath("$.email").value(authUserDTO.getEmail()));
    }

    @Test
    void registerDuplicateEmailTest() throws Exception {
        when(authUserService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException(ExceptionMessages.EMAIL_EXISTS));

        mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value(ExceptionMessages.CONFLICT))
                .andExpect(jsonPath("$.message").value(ExceptionMessages.EMAIL_EXISTS));
    }


    @Test
    void registerUserWithInvalidEmailTest() throws Exception {
        String[] invalidEmails = {"invalid-email",
                "invalid@", "in@com", "in.@com", "i@a", "@i.com",
                "invalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalid@invalid.com"
        };

        for (String email : invalidEmails) {
            RegisterRequest request = RegisterRequest.builder()
                    .email(email)
                    .password(TEST_PASSWORD)
                    .build();

            when(authUserService.register(any(RegisterRequest.class)))
                    .thenThrow(new ValidationException(ExceptionMessages.INVALID_EMAIL));

            mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value(ExceptionMessages.INVALID_EMAIL));
        }
    }

}