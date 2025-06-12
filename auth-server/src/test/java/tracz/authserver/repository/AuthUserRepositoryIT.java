package tracz.authserver.repository;

//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.test.context.ActiveProfiles;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import tracz.authserver.entity.AuthUser;
//import java.util.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_EMAIL;
//import static tracz.authserver.controller.AuthUserControllerTest.TEST_PASSWORD;
//
//@DataJpaTest
//@Testcontainers
//@ActiveProfiles("integration-test")
class AuthUserRepositoryIT {

//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine").withReuse(true);
//
//    @Autowired
//    private AuthUserRepository authUserRepository;
//
//    @AfterEach
//    void tearDown() {
//        authUserRepository.deleteAll();
//    }
//
//    @Test
//    void findByEmail() {
//        AuthUser authUser = AuthUser.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .roles(new HashSet<>(List.of("ROLE_USER")))
//                .build();
//
//        authUserRepository.save(authUser);
//
//        Optional<AuthUser> foundUser = authUserRepository.findByEmail(TEST_EMAIL);
//
//        assertThat(foundUser).isPresent();
//        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL);
//    }
//
//    @Test
//    void findByEmailNotFound() {
//        Optional<AuthUser> foundUser = authUserRepository.findByEmail(TEST_EMAIL);
//        assertThat(foundUser).isEmpty();
//    }
//
//    @Test
//    void existsByEmail() {
//        AuthUser authUser = AuthUser.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .roles(new HashSet<>(List.of("ROLE_USER")))
//                .build();
//
//        authUserRepository.save(authUser);
//        assertThat(authUserRepository.existsByEmail(TEST_EMAIL)).isTrue();
//    }
//
//    @Test
//    void existsByEmailNotFound() {
//        boolean exists = authUserRepository.existsByEmail(TEST_EMAIL);
//        assertThat(exists).isFalse();
//    }

}