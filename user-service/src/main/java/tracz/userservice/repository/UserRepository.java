package tracz.userservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracz.userservice.entity.User;


public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthServerUserId(UUID authServerUserId);
    boolean existsByEmail(String email);
    boolean existsByAuthServerUserId(UUID authServerUserId);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    @Query("SELECT u FROM User u WHERE lower(u.firstName) LIKE lower(concat('%', :query, '%')) " +
            "OR lower(u.lastName) LIKE lower(concat('%', :query, '%')) " +
            "OR lower(u.email) LIKE lower(concat('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

}
