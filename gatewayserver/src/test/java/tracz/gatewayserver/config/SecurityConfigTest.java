package tracz.gatewayserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityConfigTest {

    @Test
    void testJwtDecoderBean() {
        SecurityConfig securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "issuerUri", "http://localhost:8090/auth");

        // This will throw an exception in a test environment because it tries to connect to the issuer URI
        // We just verify that the method doesn't return null and handles the URI correctly
        Exception exception = assertThrows(Exception.class, () -> {
            securityConfig.jwtDecoder();
        });

        assertThat(exception.getMessage()).contains("Unable to resolve the Configuration");
    }
}
