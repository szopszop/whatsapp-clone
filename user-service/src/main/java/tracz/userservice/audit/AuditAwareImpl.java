package tracz.userservice.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import reactor.util.annotation.NonNullApi;
import java.util.Optional;

@Configuration("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("USER-SERVICE");
    }
}
