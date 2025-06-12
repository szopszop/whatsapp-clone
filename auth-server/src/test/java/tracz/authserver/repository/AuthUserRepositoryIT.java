package tracz.authserver.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import tracz.authserver.entity.AuthUser;
import tracz.authserver.entity.Role;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@Transactional
@DataJpaTest
@Testcontainers
@ActiveProfiles({"test", "integration-test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthUserRepositoryIT {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "Password123!";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void givenNoUser_whenFindByEmail_shouldReturnEmpty() {
        String nonExistentEmail = "nonexistent@test.com";
        Optional<AuthUser> foundUser = authUserRepository.findByEmail(nonExistentEmail);
        assertThat(foundUser).isEmpty();
    }

    @Test
    void givenUserDoesNotExist_whenExistsByEmail_thenReturnsFalse() {
        boolean exists = authUserRepository.existsByEmail("nonexistent@test.com");
        assertThat(exists).isFalse();
    }

    @Test
    void givenUserSaved_whenFindById_thenReturnsUser() {
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        AuthUser savedUser = authUserRepository.save(user);

        Optional<AuthUser> foundUser = authUserRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    void givenUserExists_whenExistsByEmail_thenReturnsTrue() {
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();

        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(Set.of(userRole))
                .build();
        authUserRepository.save(user);

        boolean exists = authUserRepository.existsByEmail(TEST_EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void givenUserSaved_whenDeleteById_thenUserIsDeleted() {
        AuthUser user = AuthUser.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        AuthUser savedUser = authUserRepository.save(user);

        authUserRepository.deleteById(savedUser.getId());
        entityManager.flush();

        Optional<AuthUser> deletedUser = authUserRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void shouldThrowException_whenSavingUserWithDuplicateEmail() {
        String duplicateEmail = "duplicate@example.com";
        AuthUser user1 = AuthUser.builder().email(duplicateEmail).password(TEST_PASSWORD).build();
        authUserRepository.save(user1);

        AuthUser user2 = AuthUser.builder().email(duplicateEmail).password(TEST_PASSWORD).build();


        assertThrows(DataIntegrityViolationException.class, () -> {
            authUserRepository.saveAndFlush(user2);
        });
    }


}