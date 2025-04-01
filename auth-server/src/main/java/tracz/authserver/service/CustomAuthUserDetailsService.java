package tracz.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.repository.AuthUserRepository;

@Service
@RequiredArgsConstructor
public class CustomAuthUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new User(
                authUser.getEmail(),
                authUser.getPassword(),
                authUser.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());
    }
}
