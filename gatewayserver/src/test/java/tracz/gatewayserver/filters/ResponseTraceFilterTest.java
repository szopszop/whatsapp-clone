package tracz.gatewayserver.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ResponseTraceFilterTest {

    @Mock
    private FilterUtility filterUtility;

    @Mock
    private GatewayFilterChain filterChain;

    private ResponseTraceFilter responseTraceFilter;
    private GlobalFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        responseTraceFilter = new ResponseTraceFilter(filterUtility);
        filter = responseTraceFilter.postGlobalFilter();

        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void testPostGlobalFilter() {
        // Create a request with a correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(FilterUtility.CORRELATION_ID, "test-correlation-id")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock the filterUtility behavior
        when(filterUtility.getCorrelationId(any(HttpHeaders.class))).thenReturn("test-correlation-id");

        // Call the filter
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the correlation ID was added to the response headers
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        assertThat(responseHeaders.get(FilterUtility.CORRELATION_ID))
                .contains("test-correlation-id");
    }

    @Test
    void testPostGlobalFilterWithNullCorrelationId() {
        // Create a request without a correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock the filterUtility behavior to return null
        when(filterUtility.getCorrelationId(any(HttpHeaders.class))).thenReturn(null);

        // Call the filter
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Verify the result
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the correlation ID header was added to the response headers with a null value
        // The implementation adds the header even if the correlation ID is null
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        assertThat(responseHeaders.containsKey(FilterUtility.CORRELATION_ID)).isTrue();
        assertThat(responseHeaders.getFirst(FilterUtility.CORRELATION_ID)).isNull();
    }
}
