package tracz.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracz.authserver.entity.BlacklistedToken;
import java.time.Instant;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByJwtId(String jwtId);
    void deleteAllByExpiryDateBefore(Instant now);
}
