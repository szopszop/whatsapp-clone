package tracz.gatewayserver.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private GatewayFilterChain filterChain;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationFilter = new AuthenticationFilter(jwtDecoder);
        
        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void testFilterWithPublicEndpoint() {
        // Create a request to a public endpoint
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/login")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Call the filter
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was called with the original exchange
        verify(filterChain).filter(exchange);
        
        // Verify that the JWT decoder was not called
        verify(jwtDecoder, never()).decode(anyString());
    }

    @Test
    void testFilterWithMissingAuthHeader() {
        // Create a request to a protected endpoint without an auth header
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/users/profile")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Call the filter
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was not called
        verify(filterChain, never()).filter(any());
        
        // Verify that the JWT decoder was not called
        verify(jwtDecoder, never()).decode(anyString());
        
        // Verify that the response status is UNAUTHORIZED
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testFilterWithValidToken() {
        // Create a mock JWT
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user@example.com");
        when(jwt.getClaimAsStringList("scope")).thenReturn(List.of("user", "admin"));
        
        // Configure the JWT decoder to return the mock JWT
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // Create a request to a protected endpoint with a valid auth header
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Call the filter
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was called with an exchange that has the user headers
        verify(filterChain).filter(argThat(ex -> {
            HttpHeaders headers = ex.getRequest().getHeaders();
            return "user@example.com".equals(headers.getFirst("X-User-Email")) &&
                   "user,admin".equals(headers.getFirst("X-User-Roles"));
        }));
        
        // Verify that the JWT decoder was called with the token
        verify(jwtDecoder).decode("valid-token");
    }

    @Test
    void testFilterWithInvalidToken() {
        // Configure the JWT decoder to throw an exception
        when(jwtDecoder.decode(anyString())).thenThrow(new JwtException("Invalid token"));
        
        // Create a request to a protected endpoint with an invalid auth header
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Call the filter
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was not called
        verify(filterChain, never()).filter(any());
        
        // Verify that the JWT decoder was called with the token
        verify(jwtDecoder).decode("invalid-token");
        
        // Verify that the response status is UNAUTHORIZED
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetOrder() {
        assertThat(authenticationFilter.getOrder()).isEqualTo(-1);
    }
}