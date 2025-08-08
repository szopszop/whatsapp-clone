package tracz.gatewayserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerConfigTest {

    @Test
    void testResourcesBean() {
        ErrorHandlerConfig errorHandlerConfig = new ErrorHandlerConfig();
        WebProperties.Resources resources = errorHandlerConfig.resources();
        assertThat(resources).isNotNull();
    }
}
