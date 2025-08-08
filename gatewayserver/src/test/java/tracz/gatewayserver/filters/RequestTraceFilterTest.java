package tracz.gatewayserver.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestTraceFilterTest {

    @Mock
    private FilterUtility filterUtility;

    @Mock
    private GatewayFilterChain filterChain;

    private RequestTraceFilter requestTraceFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestTraceFilter = new RequestTraceFilter(filterUtility);

        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void testFilterWithExistingCorrelationId() {
        // Create a request with an existing correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(FilterUtility.CORRELATION_ID, "existing-correlation-id")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock the filterUtility behavior
        when(filterUtility.getCorrelationId(any(HttpHeaders.class))).thenReturn("existing-correlation-id");

        // Call the filter
        Mono<Void> result = requestTraceFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was called with the original exchange
        verify(filterChain).filter(exchange);

        // Verify that the correlation ID was retrieved twice (once in isCorrelationIdPresent and once in the debug log) but not set
        verify(filterUtility, times(2)).getCorrelationId(any(HttpHeaders.class));
        verify(filterUtility, never()).setCorrelationId(any(), anyString());
    }

    @Test
    void testFilterWithoutCorrelationId() {
        // Create a request without a correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        MockServerWebExchange modifiedExchange = MockServerWebExchange.from(request);

        // Mock the filterUtility behavior
        when(filterUtility.getCorrelationId(any(HttpHeaders.class))).thenReturn(null);
        when(filterUtility.setCorrelationId(any(), anyString())).thenReturn(modifiedExchange);

        // Call the filter
        Mono<Void> result = requestTraceFilter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the filter chain was called with the modified exchange
        verify(filterChain).filter(modifiedExchange);

        // Verify that the correlation ID was retrieved and set
        verify(filterUtility).getCorrelationId(any(HttpHeaders.class));
        verify(filterUtility).setCorrelationId(any(), anyString());
    }
}
