package tracz.userservice.config; // Upewnij się, że nazwa pakietu jest poprawna dla user-service

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true) // Włącz adnotacje @Secured, @RolesAllowed
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") // Pobierz URI wystawcy z application.yml
    private String issuerUri;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:4200}") // Domyślne dozwolone źródła
    private String[] allowedOrigins;

    // Ścieżka do wewnętrznego API używanego przez auth-server
    private static final String INTERNAL_USERS_PATH = InternalApiPaths.USERS + "/**"; // np. /internal/api/v1/users/**

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Wyłącz CSRF dla bezstanowego API
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Konfiguracja CORS
                .authorizeHttpRequests(authorize -> authorize
                        // Zezwól na dostęp do wewnętrznego endpointu provisionUser bez uwierzytelnienia JWT
                        // Zakładamy, że ten endpoint jest chroniony na poziomie sieciowym (np. dostępny tylko dla auth-server)
                        .requestMatchers(HttpMethod.POST, InternalApiPaths.USERS).permitAll() // np. POST /internal/api/v1/users
                        .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Zezwól na dostęp do Actuatora i Swaggera
                        // Przykładowe zabezpieczenie innych endpointów
                        // .requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasRole("USER")
                        // .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // Wszystkie inne żądania wymagają uwierzytelnienia
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Bezstanowe sesje
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder()) // Użyj niestandardowego dekodera JWT
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Konwerter do ekstrakcji ról
                        )
                )
                // Możesz dodać niestandardową obsługę błędów uwierzytelniania/autoryzacji, jeśli jest potrzebna
                // np. .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(...).accessDeniedHandler(...))
                // Jednak @RestControllerAdvice z GlobalExceptionHandler powinien obsłużyć większość przypadków dla REST API.
                .exceptionHandling(Customizer.withDefaults());


        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Konfiguracja dekodera JWT na podstawie URI wystawcy (auth-server)
        // NimbusJwtDecoder pobierze JWK set z endpointu .well-known/jwks.json wystawcy
        return NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        // Alternatywnie, jeśli masz jwk-set-uri:
        // return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // Konfiguracja konwertera do mapowania claimów JWT (np. "roles" lub "scope") na GrantedAuthority
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter()); // Użyj niestandardowego konwertera
        // Możesz także ustawić principal claim name, jeśli nie jest to "sub"
        // converter.setPrincipalClaimName("preferred_username");
        return converter;
    }

    // Niestandardowy konwerter do ekstrakcji ról z claimu "roles" (lub innego, np. "realm_access.roles" dla Keycloak)
    // Dostosuj ten konwerter do struktury Twoich tokenów JWT z auth-server
    public static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            final List<String> roles = jwt.getClaimAsStringList("roles"); // Zakładamy, że role są w claimie "roles" jako lista stringów
            if (roles == null || roles.isEmpty()) {
                // Możesz także sprawdzić claim "scope" lub "scp" jeśli role są tam przekazywane
                // List<String> scopes = jwt.getClaimAsStringList("scope");
                // if (scopes != null) {
                //     return scopes.stream()
                //         .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope.toUpperCase()))
                //         .collect(Collectors.toList());
                // }
                return Collections.emptyList();
            }
            return roles.stream()
                    // Możesz chcieć dodać prefiks "ROLE_", jeśli Twoje adnotacje @Secured lub @RolesAllowed tego oczekują
                    // i jeśli auth-server nie dodaje go automatycznie.
                    // W poprzednim auth-server SecurityConfig, OAuth2TokenCustomizer dodawał role bez prefiksu.
                    .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()))
                    // lub bez prefiksu, jeśli tak wolisz: .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true); // Ustaw na true, jeśli frontend wysyła credentials (np. cookies)
        configuration.setMaxAge(3600L); // 1 godzina
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
