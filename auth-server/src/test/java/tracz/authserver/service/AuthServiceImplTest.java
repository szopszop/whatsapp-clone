package tracz.authserver.service;

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
import tracz.authserver.entity.AuthUser;
import tracz.authserver.mapper.AuthUserMapper;
import tracz.authserver.repository.AuthUserRepository;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_EMAIL;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_PASSWORD;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

//    public static final String ENCODED_PASSWORD = "encodedPassword";
//    public static final String ROLE_USER = "ROLE_USER";
//    public static final String TEST_ACCESS_TOKEN = "testAccessToken";
//    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
//
//    @Mock
//    private AuthUserRepository authUserRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private AuthUserMapper authUserMapper;
//
//    @Mock
//    private JwtEncoder jwtEncoder;
//
//    @Mock
//    private JwtDecoder jwtDecoder;
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    @Test
//    void registerUser() {
//        RegisterRequest request = RegisterRequest.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .build();
//
//        when(authUserRepository.existsByEmail(request.getEmail())).thenReturn(false);
//        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
//
//        AuthUser savedUser = AuthUser.builder()
//                .id(UUID.randomUUID())
//                .email(request.getEmail())
//                .password(ENCODED_PASSWORD)
//                .roles(new HashSet<>(List.of(ROLE_USER)))
//                .build();
//
//        AuthUserDTO expectedDTO = AuthUserDTO.builder()
//                .id(savedUser.getId())
//                .email(savedUser.getEmail())
//                .roles(savedUser.getRoles())
//                .build();
//
//        when(authUserRepository.saveAndFlush(any(AuthUser.class))).thenReturn(savedUser);
//        when(authUserMapper.authUserToDto(any(AuthUser.class))).thenReturn(expectedDTO);
//
//        AuthUserDTO result = authService.register(request);
//
//        assertThat(result).isEqualTo(expectedDTO);
//        verify(authUserRepository).existsByEmail(request.getEmail());
//        verify(passwordEncoder).encode(request.getPassword());
//        verify(authUserRepository).saveAndFlush(any(AuthUser.class));
//    }
//
//    @Test
//    void registerUserWithTakenEmail() {
//        RegisterRequest request = RegisterRequest.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .build();
//
//        when(authUserRepository.existsByEmail(request.getEmail())).thenReturn(true);
//
//        assertThatThrownBy(() -> authService.register(request))
//                .isInstanceOf(DuplicateResourceException.class)
//                .hasMessageContaining(ExceptionMessages.EMAIL_EXISTS);
//    }
//
//    @Test
//    void authenticate() {
//        AuthRequest request = AuthRequest.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .build();
//
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn(TEST_EMAIL);
//
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE_USER));
//        doAnswer(invocation -> authorities).when(authentication).getAuthorities();
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//
//        Jwt accessJwt = mock(Jwt.class);
//        when(accessJwt.getTokenValue()).thenReturn(TEST_ACCESS_TOKEN);
//
//        Jwt refreshJwt = mock(Jwt.class);
//        when(refreshJwt.getTokenValue()).thenReturn(TEST_REFRESH_TOKEN);
//
//        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
//                .thenReturn(accessJwt)
//                .thenReturn(refreshJwt);
//
//        AuthResponse response = authService.authenticate(request);
//
//        assertThat(response).isNotNull();
//        assertThat(response.getAccessToken()).isEqualTo(TEST_ACCESS_TOKEN);
//        assertThat(response.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
//
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtEncoder, times(2)).encode(any(JwtEncoderParameters.class));
//    }
//
//    @Test
//    void refreshToken() {
//        RefreshTokenRequest request = new RefreshTokenRequest();
//        request.setRefreshToken(TEST_REFRESH_TOKEN);
//
//        Jwt decodedJwt = mock(Jwt.class);
//        when(decodedJwt.getSubject()).thenReturn(TEST_EMAIL);
//        when(jwtDecoder.decode(TEST_REFRESH_TOKEN)).thenReturn(decodedJwt);
//
//        AuthUser authUser = AuthUser.builder()
//                .id(UUID.randomUUID())
//                .email(TEST_EMAIL)
//                .password(ENCODED_PASSWORD)
//                .roles(new HashSet<>(List.of(ROLE_USER)))
//                .build();
//
//        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));
//
//        Jwt accessJwt = mock(Jwt.class);
//        when(accessJwt.getTokenValue()).thenReturn(TEST_ACCESS_TOKEN);
//
//        Jwt refreshJwt = mock(Jwt.class);
//        when(refreshJwt.getTokenValue()).thenReturn(TEST_REFRESH_TOKEN);
//
//        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
//                .thenReturn(accessJwt)
//                .thenReturn(refreshJwt);
//
//        AuthResponse response = authService.refreshToken(request);
//
//        assertThat(response).isNotNull();
//        assertThat(response.getAccessToken()).isEqualTo(TEST_ACCESS_TOKEN);
//        assertThat(response.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
//
//        verify(jwtDecoder).decode(TEST_REFRESH_TOKEN);
//        verify(authUserRepository).findByEmail(TEST_EMAIL);
//        verify(jwtEncoder, times(2)).encode(any(JwtEncoderParameters.class));
//    }
}