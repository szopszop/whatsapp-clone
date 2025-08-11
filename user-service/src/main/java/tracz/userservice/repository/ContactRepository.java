package tracz.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracz.userservice.entity.Contact;
import tracz.userservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByUser(User user);
    
    List<Contact> findByUserAuthServerUserId(UUID userId);
    
    Optional<Contact> findByUserAndContact(User user, User contact);
    
    Optional<Contact> findByUserAuthServerUserIdAndContactAuthServerUserId(UUID userId, UUID contactId);
    
    boolean existsByUserAndContact(User user, User contact);
    
    boolean existsByUserAuthServerUserIdAndContactAuthServerUserId(UUID userId, UUID contactId);
}