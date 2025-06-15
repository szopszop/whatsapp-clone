package tracz.authserver.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing
@Primary
public class TestAuditConfig implements AuditorAware<String> {

    public static final String TEST_USER = "test-user";

    public @NotNull Optional<String> getCurrentAuditor() {
            return Optional.of(TEST_USER);

    }
}