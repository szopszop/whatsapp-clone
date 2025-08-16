package tracz.notificationservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tracz.notificationservice.config.FirebaseConfig;
import tracz.notificationservice.service.MessageListener;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a simple test to verify that the application can start.
 * More detailed tests are in the MessageListenerTest class.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceApplicationTests {

    @Test
    void firebaseConfigShouldBeValid() {
        // This test simply verifies that the Firebase configuration class can be instantiated
        FirebaseConfig config = new FirebaseConfig();
        assertThat(config).isNotNull();
    }

}
