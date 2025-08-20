package tracz.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;
import java.time.Duration;

@SpringBootApplication
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-server-route", p -> p
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                            .requestRateLimiter(c -> c
                                .setRateLimiter(anonymousRateLimiter())
                                .setKeyResolver(userKeyResolver())))
                        .uri("http://auth-server:8090")
                )

                .route("user-profile-route", p -> p
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(authenticatedRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                                .circuitBreaker(c -> c
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user-service")))
                        .uri("lb://USER-SERVICE")
                )

                .route("message-service-route", p -> p
                        .path("/api/v1/messages/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/messages/(?<segment>.*)", "/${segment}")
                            .requestRateLimiter(c -> c
                                .setRateLimiter(authenticatedRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                            .circuitBreaker(c -> c
                                .setName("messageServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/message-service")))
                        .uri("lb://MESSAGE-SERVICE")
                )
                .route("message-service-websocket-route", p -> p
                        .path("/ws/**")
                        .filters(f -> f
                            .requestRateLimiter(c -> c
                                .setRateLimiter(authenticatedRateLimiter())
                                .setKeyResolver(userKeyResolver())))
                        .uri("lb:ws://MESSAGE-SERVICE")
                )
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
    }

    @Bean
    public RedisRateLimiter authenticatedRateLimiter() {
        return new RedisRateLimiter(5, 5, 1);
    }

    @Bean
    public RedisRateLimiter anonymousRateLimiter() {
        return new RedisRateLimiter(2, 2, 1);
    }

    @Primary
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return authenticatedRateLimiter();
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .flatMap(p -> Mono.just(p.getName()))
                .defaultIfEmpty("anonymous");
    }
}
