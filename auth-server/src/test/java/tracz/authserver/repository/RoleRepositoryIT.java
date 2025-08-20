package tracz.authserver.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import tracz.authserver.config.TestAuditConfig;
import tracz.authserver.entity.Role;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tracz.authserver.config.TestAuditConfig.TEST_USER;

@Transactional
@DataJpaTest
@Testcontainers
@ActiveProfiles({"test", "integration-test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestAuditConfig.class)
class RoleRepositoryIT {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_shouldReturnRole_whenRoleExists() {

        Optional<Role> foundUserRole = roleRepository.findByName(ROLE_USER);
        Optional<Role> foundAdminRole = roleRepository.findByName(ROLE_ADMIN);

        assertThat(foundUserRole).isPresent();
        assertThat(foundUserRole.get().getName()).isEqualTo(ROLE_USER);
        assertThat(foundUserRole.get().getCreatedBy()).isEqualTo("system");

        assertThat(foundAdminRole).isPresent();
        assertThat(foundAdminRole.get().getName()).isEqualTo(ROLE_ADMIN);
    }

    @Test
    void findByName_shouldReturnEmpty_whenRoleDoesNotExist() {
        Optional<Role> foundRole = roleRepository.findByName("ROLE_X");
        assertThat(foundRole).isEmpty();
    }

    @Test
    void shouldThrowException_whenSavingRoleWithDuplicateName() {
        String duplicateName = "ROLE_DUPLICATE";
        Role role1 = Role.builder().name(duplicateName).build();
        roleRepository.saveAndFlush(role1);
        entityManager.clear();

        Role role2 = Role.builder().name(duplicateName).build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            roleRepository.saveAndFlush(role2);
        });
    }

    @Test
    void shouldSaveRoleWithValidData() {
        String roleName = "ROLE_TEST_ROLE";
        Role role = Role.builder().name(roleName).build();

        Role savedRole = roleRepository.saveAndFlush(role);

        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(roleName);
        assertThat(savedRole.getCreatedAt()).isNotNull();
        assertThat(savedRole.getCreatedBy()).isEqualTo(TEST_USER);
        assertThat(savedRole.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteRoleById() {
        Role role = Role.builder().name("ROLE_TO_DELETE").build();
        Role savedRole = roleRepository.saveAndFlush(role);

        roleRepository.deleteById(savedRole.getId());
        roleRepository.flush();

        Optional<Role> deletedRole = roleRepository.findById(savedRole.getId());
        assertThat(deletedRole).isEmpty();
    }
}