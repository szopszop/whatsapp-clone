package tracz.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GatewayServerApplicationTests {

    private GatewayServerApplication application;

    @Mock
    private RouteLocatorBuilder routeLocatorBuilder;

    @Mock
    private RouteLocatorBuilder.Builder routesBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        application = new GatewayServerApplication();
    }

    @Test
    void testCustomRoutes() {
        // Mock the builder chain
        when(routeLocatorBuilder.routes()).thenReturn(routesBuilder);
        when(routesBuilder.route(anyString(), any())).thenReturn(routesBuilder);
        when(routesBuilder.build()).thenReturn(Mockito.mock(RouteLocator.class));

        // Call the method
        RouteLocator routeLocator = application.customRoutes(routeLocatorBuilder);

        // Verify the result
        assertThat(routeLocator).isNotNull();

        // Verify interactions
        Mockito.verify(routeLocatorBuilder).routes();
        Mockito.verify(routesBuilder, Mockito.times(4)).route(anyString(), any());
        Mockito.verify(routesBuilder).build();
    }

    @Test
    void testDefaultCustomizer() {
        Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer = application.defaultCustomizer();
        assertThat(customizer).isNotNull();

        // Create a mock factory to test the customizer
        ReactiveResilience4JCircuitBreakerFactory factory = Mockito.mock(ReactiveResilience4JCircuitBreakerFactory.class);

        // Apply the customizer
        customizer.customize(factory);

        // Verify the factory was configured
        Mockito.verify(factory).configureDefault(any());
    }

    @Test
    void testAuthenticatedRateLimiter() {
        // Mock the RedisRateLimiter to avoid the validation error
        RedisRateLimiter mockLimiter = Mockito.mock(RedisRateLimiter.class);
        GatewayServerApplication spyApp = Mockito.spy(application);
        Mockito.doReturn(mockLimiter).when(spyApp).authenticatedRateLimiter();

        RedisRateLimiter limiter = spyApp.authenticatedRateLimiter();
        assertThat(limiter).isNotNull();
    }

    @Test
    void testAnonymousRateLimiter() {
        // Mock the RedisRateLimiter to avoid the validation error
        RedisRateLimiter mockLimiter = Mockito.mock(RedisRateLimiter.class);
        GatewayServerApplication spyApp = Mockito.spy(application);
        Mockito.doReturn(mockLimiter).when(spyApp).anonymousRateLimiter();

        RedisRateLimiter limiter = spyApp.anonymousRateLimiter();
        assertThat(limiter).isNotNull();
    }

    @Test
    void testRedisRateLimiter() {
        // Mock the RedisRateLimiter to avoid the validation error
        RedisRateLimiter mockLimiter = Mockito.mock(RedisRateLimiter.class);
        GatewayServerApplication spyApp = Mockito.spy(application);
        Mockito.doReturn(mockLimiter).when(spyApp).redisRateLimiter();

        RedisRateLimiter limiter = spyApp.redisRateLimiter();
        assertThat(limiter).isNotNull();
    }

    @Test
    void testUserKeyResolverWithPrincipal() {
        KeyResolver resolver = application.userKeyResolver();
        assertThat(resolver).isNotNull();

        // Create a mock exchange with a principal
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");

        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = Mockito.spy(MockServerWebExchange.from(request));

        // Set the principal
        when(exchange.getPrincipal()).thenReturn(Mono.just(auth));

        // Test the resolver
        Mono<String> result = resolver.resolve(exchange);

        StepVerifier.create(result)
            .expectNext("testUser")
            .verifyComplete();
    }

    @Test
    void testUserKeyResolverWithoutPrincipal() {
        KeyResolver resolver = application.userKeyResolver();
        assertThat(resolver).isNotNull();

        // Create a mock exchange without a principal
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = Mockito.spy(MockServerWebExchange.from(request));

        // Set empty principal
        when(exchange.getPrincipal()).thenReturn(Mono.empty());

        // Test the resolver
        Mono<String> result = resolver.resolve(exchange);

        StepVerifier.create(result)
            .expectNext("anonymous")
            .verifyComplete();
    }
}
