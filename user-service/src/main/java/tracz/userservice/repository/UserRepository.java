package tracz.userservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tracz.userservice.entity.User;


public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthServerUserId(UUID authServerUserId);
    boolean existsByEmail(String email);
    boolean existsByAuthServerUserId(UUID authServerUserId);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
