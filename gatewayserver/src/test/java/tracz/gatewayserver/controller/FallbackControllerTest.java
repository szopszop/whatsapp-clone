package tracz.gatewayserver.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FallbackControllerTest {

    private FallbackController fallbackController;

    @BeforeEach
    void setUp() {
        fallbackController = new FallbackController();
    }

    @Test
    void testUserServiceFallback() {
        // Test the controller method directly
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.userServiceFallback();

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    Map<String, Object> body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("status")).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
                    assertThat(body.get("error")).isEqualTo("Service Unavailable");
                    assertThat(body.get("message")).isEqualTo("User service is currently unavailable. Please try again later.");
                    assertThat(body.get("path")).isEqualTo("/api/v1/users");
                })
                .verifyComplete();
    }

    @Test
    void testMessageServiceFallback() {
        // Test the controller method directly
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.messageServiceFallback();

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    Map<String, Object> body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("status")).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
                    assertThat(body.get("error")).isEqualTo("Service Unavailable");
                    assertThat(body.get("message")).isEqualTo("Message service is currently unavailable. Please try again later.");
                    assertThat(body.get("path")).isEqualTo("/api/v1/messages");
                })
                .verifyComplete();
    }
}
