package tracz.authserver.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.AuthRequest;
import tracz.authserver.dto.AuthResponse;
import tracz.authserver.service.AuthUserService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "unit-test"})
@WebMvcTest(AuthUserController.class)
public class LoginControllerTest {

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
    void authenticate_whenValidCredentials_shouldReturnOk() throws Exception {
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthResponse authResponse = new AuthResponse(ACCESS_TOKEN, REFRESH_TOKEN);

        when(authUserService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.TEXT_HTML));
    }

    @Test
    void authenticate_whenInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest(TEST_EMAIL, "wrongpassword");

        when(authUserService.authenticate(any(AuthRequest.class)))
                .thenThrow(new AuthenticationException(ExceptionMessages.BAD_CREDENTIALS){});

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
