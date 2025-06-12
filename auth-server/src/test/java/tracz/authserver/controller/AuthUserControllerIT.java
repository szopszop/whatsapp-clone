package tracz.authserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.dto.*;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.entity.Role;
import tracz.authserver.repository.*;
import tracz.authserver.service.client.UserServiceFeignClient;
import java.util.Set;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@ActiveProfiles({"test", "integration-tests"})
class AuthUserControllerIT {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "Password@123";
    public static final String ROLE_USER = "ROLE_USER";

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .build();
        when(userServiceFeignClient.provisionUser(any()))
                .thenReturn(ResponseEntity.ok().build());
    }

    @AfterEach
    void tearDown() {
        blacklistedTokenRepository.deleteAll();
        authUserRepository.deleteAll();
    }


    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserServiceFeignClient userServiceFeignClient;

    @Test
    void register_whenValidRequest_shouldCreateUserAndReturnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(ApiPaths.REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email").value(request.email()));

        assertTrue(authUserRepository.findByEmail(request.email()).isPresent());
    }

    @Test
    void register_whenUserAlreadyExists_shouldReturnConflict() throws Exception {
        Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow();
        AuthUser existingUser = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(ApiPaths.REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void authenticate_whenValidCredentials_shouldReturnTokens() throws Exception {
        Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow();
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(user);

        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    void authenticate_whenInvalidPassword_shouldReturnUnauthorized() throws Exception {
        Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow();
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(user);

        AuthRequest request = new AuthRequest(TEST_EMAIL, "wrongPassword");

        mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_whenValid_shouldReturnNewTokens() throws Exception {
        Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow();
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(user);

        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        MvcResult authResult = mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(authResult.getResponse().getContentAsString(),
                AuthResponse.class);
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(authResponse.refreshToken());

        mockMvc.perform(post(ApiPaths.REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    void refreshToken_whenInvalid_shouldReturnUnauthorized() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-token");

        mockMvc.perform(post(ApiPaths.REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_whenValidToken_shouldBlacklistTokenAndReturnOk() throws Exception {

        Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow();
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(user);

        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        MvcResult authResult = mockMvc.perform(post(ApiPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(authResult.getResponse().getContentAsString(),
                AuthResponse.class);
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest(authResponse.refreshToken());

        assertFalse(blacklistedTokenRepository.findAll().iterator().hasNext());

        mockMvc.perform(post(ApiPaths.LOGOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());

        assertTrue(blacklistedTokenRepository.findAll().iterator().hasNext());
    }
}
