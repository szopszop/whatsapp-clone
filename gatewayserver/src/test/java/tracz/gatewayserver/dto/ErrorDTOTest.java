package tracz.gatewayserver.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testErrorDTOWithoutValidationErrors() {
        Instant now = Instant.now();
        ErrorDTO errorDTO = new ErrorDTO(
                now,
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid input",
                "/api/test"
        );

        assertThat(errorDTO.timestamp()).isEqualTo(now);
        assertThat(errorDTO.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorDTO.error()).isEqualTo("Bad Request");
        assertThat(errorDTO.message()).isEqualTo("Invalid input");
        assertThat(errorDTO.path()).isEqualTo("/api/test");
        assertThat(errorDTO.validationErrors()).isNull();
    }

    @Test
    void testErrorDTOWithValidationErrors() {
        Instant now = Instant.now();
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("email", "must be a valid email");
        validationErrors.put("name", "must not be blank");

        ErrorDTO errorDTO = new ErrorDTO(
                now,
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                "/api/test",
                validationErrors
        );

        assertThat(errorDTO.timestamp()).isEqualTo(now);
        assertThat(errorDTO.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorDTO.error()).isEqualTo("Bad Request");
        assertThat(errorDTO.message()).isEqualTo("Validation failed");
        assertThat(errorDTO.path()).isEqualTo("/api/test");
        assertThat(errorDTO.validationErrors()).isEqualTo(validationErrors);
    }

    @Test
    void testJsonSerialization() throws Exception {
        Instant now = Instant.now();
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("email", "must be a valid email");

        ErrorDTO errorDTO = new ErrorDTO(
                now,
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                "/api/test",
                validationErrors
        );

        String json = objectMapper.writeValueAsString(errorDTO);

        assertThat(json).contains("\"timestamp\":");
        assertThat(json).contains("\"status\":400");
        assertThat(json).contains("\"error\":\"Bad Request\"");
        assertThat(json).contains("\"message\":\"Validation failed\"");
        assertThat(json).contains("\"path\":\"/api/test\"");
        assertThat(json).contains("\"validationErrors\":{\"email\":\"must be a valid email\"}");
    }

    @Test
    void testJsonSerializationWithoutValidationErrors() throws Exception {
        Instant now = Instant.now();

        ErrorDTO errorDTO = new ErrorDTO(
                now,
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid input",
                "/api/test"
        );

        String json = objectMapper.writeValueAsString(errorDTO);

        assertThat(json).contains("\"timestamp\":");
        assertThat(json).contains("\"status\":400");
        assertThat(json).contains("\"error\":\"Bad Request\"");
        assertThat(json).contains("\"message\":\"Invalid input\"");
        assertThat(json).contains("\"path\":\"/api/test\"");
        assertThat(json).doesNotContain("\"validationErrors\"");
    }
}
