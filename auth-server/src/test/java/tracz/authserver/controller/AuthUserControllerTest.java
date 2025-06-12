package tracz.authserver.controller;

import java.time.Instant;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.core.AuthenticationException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
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
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthUserService;

@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthUserController.class)
@ActiveProfiles("unit-test")
public class AuthUserControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @MockitoBean
//    private UserDetailsService userDetailsService;
//
//    @MockitoBean
//    private RegisteredClientRepository registeredClientRepository;
//
//    @MockitoBean
//    private AuthUserService authUserService;
//
//    private AuthUserController authUserController;
//
//    static final String EMAIL = "email";
//    public static final String TEST_EMAIL = "test@test.com";
//    public static final String TEST_PASSWORD = "PasswordPassword123!";
//    static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
//            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));
//
//    UUID authUserId;
//    AuthUserDTO authUserDTO;
//    RegisterRequest request;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .build();
//        authUserId = UUID.randomUUID();
//        authUserDTO = AuthUserDTO.builder()
//                .id(authUserId)
//                .email(TEST_EMAIL)
//                .roles(new HashSet<>(List.of("ROLE_USER")))
//                .build();
//        request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);
//        authUserController = new AuthUserController(authUserService);
//    }
//
//    @Test
//    void registerUserTest() throws Exception {
//        when(authUserService.register(any(RegisterRequest.class)))
//                .thenReturn(authUserDTO);
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(authUserId.toString()))
//                .andExpect(jsonPath("$.email").value(authUserDTO.getEmail()));
//    }
//
//    @Test
//    void registerDuplicateEmailTest() throws Exception {
//        when(authUserService.register(any(RegisterRequest.class)))
//                .thenThrow(new DuplicateResourceException(ExceptionMessages.EMAIL_EXISTS));
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.status").value(409))
//                .andExpect(jsonPath("$.error").value(ExceptionMessages.CONFLICT))
//                .andExpect(jsonPath("$.message").value(ExceptionMessages.EMAIL_EXISTS));
//    }
//
//
//    @Test
//    void registerUserWithInvalidEmailTest() throws Exception {
//        String[] invalidEmails = {"invalid-email",
//                "invalid@", "in@com", "in.@com", "i@a", "@i.com",
//                "invalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalid@invalid.com"
//        };
//
//        for (String email : invalidEmails) {
//            RegisterRequest request = RegisterRequest.builder()
//                    .email(email)
//                    .password(TEST_PASSWORD)
//                    .build();
//
//            when(authUserService.register(any(RegisterRequest.class)))
//                    .thenThrow(new ValidationException(ExceptionMessages.INVALID_EMAIL));
//
//            mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
//                            .with(csrf())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.message").value(ExceptionMessages.INVALID_EMAIL));
//        }
//    }
//
//    @Test
//    void registerUserWithInvalidPasswordTest() throws Exception {
//        String[] invalidPasswords = {"invalid-password", "1111111111111", "Password123", "Password!!!"};
//
//        for (String password : invalidPasswords) {
//            RegisterRequest request = RegisterRequest.builder()
//                    .email(TEST_EMAIL)
//                    .password(password)
//                    .build();
//
//            when(authUserService.register(any(RegisterRequest.class)))
//                    .thenThrow(new ValidationException(ExceptionMessages.PASSWORD_CONSTRAINT));
//
//            mockMvc.perform(post(ApiPaths.API_AUTH_REGISTER)
//                            .with(csrf())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.message").value(ExceptionMessages.PASSWORD_CONSTRAINT));
//        }
//    }
//
//    @Test
//    void authenticateUserTest() throws Exception {
//        AuthRequest request = AuthRequest.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .build();
//
//        AuthResponse response = AuthResponse.builder()
//                .accessToken("test-access-token")
//                .refreshToken("test-refresh-token")
//                .build();
//
//        when(authUserService.authenticate(any(AuthRequest.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_LOGIN)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
//                .andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()));
//    }
//
//    @Test
//    void authenticateUserWithInvalidCredentialTest() throws Exception {
//        AuthRequest request = AuthRequest.builder()
//                .email(TEST_EMAIL)
//                .password("wrong-password")
//                .build();
//
//        when(authUserService.authenticate(any(AuthRequest.class)))
//                .thenThrow(new AuthenticationException(ExceptionMessages.BAD_CREDENTIALS) {});
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_LOGIN)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.status").value(401))
//                .andExpect(jsonPath("$.message").value(ExceptionMessages.BAD_CREDENTIALS));
//    }
//
//    @Test
//    void refreshTokenTest() throws Exception {
//        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
//        refreshRequest.setRefreshToken("refresh-token");
//
//        AuthResponse response = AuthResponse.builder()
//                .accessToken("test-access-token")
//                .refreshToken("test-refresh-token")
//                .build();
//
//        when(authUserService.refreshToken(refreshRequest)).thenReturn(response);
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_REFRESH)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
//                .andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()));
//    }
//
//    @Test
//    void refreshTokenWithInvalidRefreshTokenTest() throws Exception {
//        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
//        refreshRequest.setRefreshToken("invalid-token");
//
//        when(authUserService.refreshToken(refreshRequest))
//                .thenThrow(new AuthenticationException(ExceptionMessages.INVALID_TOKEN) {});
//
//        mockMvc.perform(post(ApiPaths.API_AUTH_REFRESH)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.status").value(401))
//                .andExpect(jsonPath("$.message").value(ExceptionMessages.INVALID_TOKEN));
//    }
}