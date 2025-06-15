package tracz.authserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.*;
import tracz.authserver.exception.UserAlreadyExistsException;
import tracz.authserver.service.AuthUserService;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test", "unit-test"})
@WebMvcTest(AuthUserController.class)
class AuthUserControllerTest {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "Password@123";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUserService authUserService;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void register_whenValidRequest_shouldReturnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthUserDTO authUserDTO = new AuthUserDTO(UUID.randomUUID(), TEST_EMAIL, Collections.emptySet());

        when(authUserService.register(any(RegisterRequest.class))).thenReturn(authUserDTO);

        mockMvc.perform(post(ApiPaths.REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(authUserDTO.id().toString()))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(header().string("Location", ApiPaths.REGISTER + "/" + authUserDTO.id()));
    }

    @Test
    void register_whenInvalidEmail_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid-email", "Password@123");

        mockMvc.perform(post(ApiPaths.REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("must be a well-formed email address"));
    }

    @Test
    void authenticate_whenValidCredentials_shouldReturnOk() throws Exception {
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthResponse authResponse = new AuthResponse(ACCESS_TOKEN, REFRESH_TOKEN);

        when(authUserService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    void authenticate_whenInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest(TEST_EMAIL, "wrongpassword");

        when(authUserService.authenticate(any(AuthRequest.class)))
                .thenThrow(new AuthenticationException(ExceptionMessages.BAD_CREDENTIALS){});

        mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_whenValidToken_shouldReturnOk() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");
        AuthResponse authResponse = new AuthResponse("newAccessToken", "newRefreshToken");

        when(authUserService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post(ApiPaths.REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    void refreshToken_whenTokenIsInvalid_shouldReturnUnauthorized() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("invalidRefreshToken");

        when(authUserService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new AuthenticationException(ExceptionMessages.INVALID_TOKEN){});

        mockMvc.perform(post(ApiPaths.REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_whenTokenIsEmpty_shouldReturnBadRequest() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("");

        mockMvc.perform(post(ApiPaths.REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_whenValidToken_shouldReturnOk() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");

        doNothing().when(authUserService).logout(any(RefreshTokenRequest.class));

        mockMvc.perform(post(ApiPaths.LOGOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}
