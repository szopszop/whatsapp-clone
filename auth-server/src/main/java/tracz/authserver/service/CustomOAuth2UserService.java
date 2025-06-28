package tracz.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.entity.Role;
import tracz.authserver.repository.AuthUserRepository;
import tracz.authserver.repository.RoleRepository;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String email = oauth2User.getAttribute("email");

        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseGet(() -> {
                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new IllegalStateException("Rola ROLE_USER nie istnieje w bazie danych."));
                    AuthUser newUser = AuthUser.builder()
                            .email(email)
                            .password("")
                            .roles(Set.of(userRole))
                            .build();
                    return authUserRepository.save(newUser);
                });

        Set<SimpleGrantedAuthority> authorities = authUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new DefaultOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                "email"
        );
    }
}
