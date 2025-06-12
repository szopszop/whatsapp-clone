package tracz.authserver.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tracz.authserver.config.TestAuditConfig;
import tracz.authserver.entity.BlacklistedToken;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles({"test", "integration-test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestAuditConfig.class)
public class BlacklistedTokenRepositoryIT {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        blacklistedTokenRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void existsByJwtId_shouldReturnTrue_whenTokenExists() {
        String jwtId = UUID.randomUUID().toString();
        BlacklistedToken token = BlacklistedToken.builder()
                .jwtId(jwtId)
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        blacklistedTokenRepository.save(token);

        boolean exists = blacklistedTokenRepository.existsByJwtId(jwtId);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByJwtId_shouldReturnFalse_whenTokenDoesNotExist() {
        boolean exists = blacklistedTokenRepository.existsByJwtId(UUID.randomUUID().toString());
        assertThat(exists).isFalse();
    }

    @Test
    void deleteAllByExpiryDateBefore_shouldDeleteExpiredTokens() {
        Instant now = Instant.now();
        String expiredJwtId = "expired-jwt-id";
        String validJwtId = "valid-jwt-id";

        BlacklistedToken expiredToken = BlacklistedToken.builder()
                .jwtId(expiredJwtId)
                .expiryDate(now.minus(1, ChronoUnit.MINUTES))
                .build();

        BlacklistedToken validToken = BlacklistedToken.builder()
                .jwtId(validJwtId)
                .expiryDate(now.plus(1, ChronoUnit.DAYS))
                .build();

        blacklistedTokenRepository.save(expiredToken);
        blacklistedTokenRepository.save(validToken);
        assertThat(blacklistedTokenRepository.count()).isEqualTo(2);

        blacklistedTokenRepository.deleteAllByExpiryDateBefore(now);

        assertThat(blacklistedTokenRepository.count()).isEqualTo(1);
        assertThat(blacklistedTokenRepository.existsByJwtId(expiredJwtId)).isFalse();
        assertThat(blacklistedTokenRepository.existsByJwtId(validJwtId)).isTrue();
    }

    @Test
    void deleteAllByExpiryDateBefore_shouldNotDeleteFutureTokens() {
        Instant now = Instant.now();
        BlacklistedToken token1 = BlacklistedToken.builder()
                .jwtId(UUID.randomUUID().toString())
                .expiryDate(now.plus(1, ChronoUnit.HOURS))
                .build();
        BlacklistedToken token2 = BlacklistedToken.builder()
                .jwtId(UUID.randomUUID().toString())
                .expiryDate(now.plus(2, ChronoUnit.HOURS))
                .build();

        blacklistedTokenRepository.save(token1);
        blacklistedTokenRepository.save(token2);
        assertThat(blacklistedTokenRepository.count()).isEqualTo(2);

        blacklistedTokenRepository.deleteAllByExpiryDateBefore(now);

        assertThat(blacklistedTokenRepository.count()).isEqualTo(2);
    }
}