package tracz.authserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.entity.Role;
import tracz.authserver.repository.AuthUserRepository;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthUserDetailsServiceTest {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String TEST_EMAIL = "sa@sa.sa";
    private static final String ANOTHER_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password@123";

    @Mock
    private AuthUserRepository authUserRepository;

    @InjectMocks
    private CustomAuthUserDetailsService userDetailsService;

    @Test
    void shouldReturnUserDetailsForUserWithSingleRole() {
        Role userRole = Role.builder().name(ROLE_USER).build();
        AuthUser authUser = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of(userRole))
                .build();

        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
        assertThat(userDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(userDetails.getAuthorities())
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(ROLE_USER);

        verify(authUserRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void shouldReturnUserDetailsForUserWithMultipleRoles() {
        Role userRole = Role.builder().name(ROLE_USER).build();
        Role adminRole = Role.builder().name(ROLE_ADMIN).build();
        AuthUser authUser = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of(userRole, adminRole))
                .build();

        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
        assertThat(userDetails.getAuthorities())
                .hasSize(2)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(ROLE_USER, ROLE_ADMIN);
    }

    @Test
    void shouldReturnUserDetailsForUserWithNoRoles() {
        AuthUser authUser = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of())
                .build();

        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(TEST_EMAIL))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(TEST_EMAIL);

        verify(authUserRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void shouldHandleNullEmailGracefully() {
        when(authUserRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("null");
    }

    @Test
    void shouldHandleEmptyEmailGracefully() {
        String emptyEmail = "";
        when(authUserRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(emptyEmail);
    }

    @Test
    void shouldCallRepositoryOnlyOncePerCall() {
        Role userRole = Role.builder().name(ROLE_USER).build();
        AuthUser authUser = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of(userRole))
                .build();

        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));

        userDetailsService.loadUserByUsername(TEST_EMAIL);

        verify(authUserRepository, times(1)).findByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(authUserRepository);
    }

    @Test
    void shouldWorkWithDifferentEmails() {
        Role userRole = Role.builder().name(ROLE_USER).build();
        AuthUser authUser = AuthUser.builder()
                .email(ANOTHER_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of(userRole))
                .build();

        when(authUserRepository.findByEmail(ANOTHER_EMAIL)).thenReturn(Optional.of(authUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(ANOTHER_EMAIL);

        assertThat(userDetails.getUsername()).isEqualTo(ANOTHER_EMAIL);
        verify(authUserRepository).findByEmail(ANOTHER_EMAIL);
    }
}