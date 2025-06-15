package tracz.authserver.service;

import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import tracz.authserver.config.ExceptionMessages;
import tracz.authserver.dto.*;
import tracz.authserver.exception.UserAlreadyExistsException;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.AuthUserRepository;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceImplTest {

    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String TEST_ACCESS_TOKEN = "testAccessToken";
    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    private static final  String TEST_EMAIL = "email";
    private static final  String TEST_PASSWORD = "email";

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUserMapper authUserMapper;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private AuthUserServiceImpl authService;


    @Test
    void registerUserWithTakenEmail() {
        RegisterRequest request = new RegisterRequest(TEST_EMAIL,TEST_PASSWORD);

        when(authUserRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(ExceptionMessages.EMAIL_EXISTS);
    }

    @Test
    void authenticate() {
        AuthRequest request = new AuthRequest(TEST_EMAIL,TEST_PASSWORD);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(TEST_EMAIL);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE_USER));
        doAnswer(invocation -> authorities).when(authentication).getAuthorities();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Jwt accessJwt = mock(Jwt.class);
        when(accessJwt.getTokenValue()).thenReturn(TEST_ACCESS_TOKEN);

        Jwt refreshJwt = mock(Jwt.class);
        when(refreshJwt.getTokenValue()).thenReturn(TEST_REFRESH_TOKEN);

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(accessJwt)
                .thenReturn(refreshJwt);

        AuthResponse response = authService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(TEST_ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(TEST_REFRESH_TOKEN);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtEncoder, times(2)).encode(any(JwtEncoderParameters.class));
    }


}