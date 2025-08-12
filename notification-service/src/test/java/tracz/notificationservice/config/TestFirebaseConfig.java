package tracz.notificationservice.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestFirebaseConfig {

    @Bean
    @Primary
    public FirebaseApp firebaseApp() {
        return Mockito.mock(FirebaseApp.class);
    }

    @Bean
    @Primary
    public FirebaseMessaging firebaseMessaging() {
        return Mockito.mock(FirebaseMessaging.class);
    }
}