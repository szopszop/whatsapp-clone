package tracz.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${app.firebase.service-account-file}")
    private String serviceAccountFile;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            Resource resource = new ClassPathResource(serviceAccountFile);

            if (!resource.exists()) {
                log.warn("Firebase service account file not found: {}. Firebase messaging will not be available.", serviceAccountFile);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    return FirebaseApp.initializeApp(options);
                } else {
                    return FirebaseApp.getInstance();
                }
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    return FirebaseApp.initializeApp(options);
                } else {
                    return FirebaseApp.getInstance();
                }
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase", e);
            throw new RuntimeException("Error initializing Firebase", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
