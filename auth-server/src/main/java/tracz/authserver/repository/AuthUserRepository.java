package tracz.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracz.authserver.entity.AuthUser;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
