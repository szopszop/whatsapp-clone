package tracz.authserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.repository.AuthUserRepository;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_EMAIL;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_PASSWORD;

@ExtendWith(MockitoExtension.class)
class CustomAuthUserDetailsServiceTest {

//    @Mock
//    private AuthUserRepository authUserRepository;
//
//    @InjectMocks
//    private CustomAuthUserDetailsService userDetailsService;
//
//    @Test
//    void shouldReturnUserDetails() throws Exception {
//        AuthUser authUser = AuthUser.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .roles(new HashSet<>(List.of("ROLE_USER")))
//                .build();
//
//        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(authUser));
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
//
//        assertThat(userDetails).isNotNull();
//        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
//        assertThat(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).isTrue();
//    }
//
//    @Test
//    void shouldThrowExceptionWhenUserDoesNotExist() {
//        when(authUserRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(TEST_EMAIL))
//                .isInstanceOf(UsernameNotFoundException.class)
//                .hasMessageContaining(TEST_EMAIL);
//    }


}