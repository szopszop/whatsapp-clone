package tracz.authserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import java.time.Duration;
import java.util.UUID;


@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableFeignClients
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Whatsapp Auth Server REST API Documentation",
                description = "Whatsapp Auth Server microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Szymon Tracz",
                        email = "szymontracz1@gmail.com",
                        url = "https://www.github.com/szopszop"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://www.github.com/szopszop"
                )
        )
)
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    // Ten bean służy do inicjalizacji klientów w bazie danych przy pierwszym uruchomieniu.
    // W środowisku produkcyjnym klienci powinni być zarządzani przez skrypty administracyjne.
    @Bean
    public CommandLineRunner initClients(RegisteredClientRepository repository, PasswordEncoder passwordEncoder,
                                         @Value("${auth-server.internal.client-secret}") String authServerInternalSecret) {
        return args -> {
            String angularClientId = "angular-ui";
            if (repository.findByClientId(angularClientId) == null) {
                RegisteredClient angularPkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(angularClientId)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://localhost:4200/callback")
                        .postLogoutRedirectUri("http://localhost:4200/")
                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .scope(OidcScopes.EMAIL)
                        .scope("user.read")
                        .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(15))
                                .refreshTokenTimeToLive(Duration.ofHours(8))
                                .reuseRefreshTokens(false)
                                .build())
                        .build();
                repository.save(angularPkceClient);
            }

            String internalClientId = "auth-server-internal";
            if (repository.findByClientId(internalClientId) == null) {
                RegisteredClient authServerInternalClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(internalClientId)
                        .clientSecret(passwordEncoder.encode(authServerInternalSecret))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .scope("internal.user.read")
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(60))
                                .build())
                        .build();
                repository.save(authServerInternalClient);
            }
        };
    }

}
