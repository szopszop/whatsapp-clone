package tracz.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tracz.userservice.entity.Role;
import tracz.userservice.entity.User;
import tracz.userservice.repository.UserRepository;
import java.util.UUID;

@Profile("integration-test")
@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        loadTestData();
    }

    @PostConstruct
    private void loadTestData() {
        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("useremail" + i + "@email.com")
                    .password("SercurePassword123!" + i)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }
        User admin = User.builder()
                .id(UUID.randomUUID())
                .email("adminemail@email.com")
                .password("SercurePassword123!")
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }
}
