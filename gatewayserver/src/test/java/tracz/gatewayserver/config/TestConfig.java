package tracz.gatewayserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@Configuration
@ActiveProfiles("test")
public class TestConfig {

    /**
     * Provides a WebProperties.Resources bean for testing
     */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    /**
     * Provides a mock JwtDecoder for testing
     */
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }

    /**
     * Configures ObjectMapper with JavaTimeModule for proper Instant serialization
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}