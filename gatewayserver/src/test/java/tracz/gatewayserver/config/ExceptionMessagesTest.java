package tracz.gatewayserver.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionMessagesTest {

    @Test
    void testExceptionMessages() {
        assertThat(ExceptionMessages.ROUTE_NOT_FOUND).isEqualTo("Route not found");
        assertThat(ExceptionMessages.SERVICE_UNAVAILABLE).isEqualTo("Service unavailable");
        assertThat(ExceptionMessages.TIMEOUT).isEqualTo("Request timeout");
        assertThat(ExceptionMessages.VALIDATION_FAILED).isEqualTo("Validation Failed");
        assertThat(ExceptionMessages.UNAUTHORIZED_MESSAGE).isEqualTo("Authentication required to access this resource.");
        assertThat(ExceptionMessages.FORBIDDEN_MESSAGE).isEqualTo("You do not have permission to access this resource.");
        assertThat(ExceptionMessages.INTERNAL_ERROR).isEqualTo("An unexpected internal server error occurred.");
        assertThat(ExceptionMessages.LIMIT_EXCEEDED).isEqualTo("Rate limit exceeded. Please try again later.");
    }
}