package tracz.notificationservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Whatsapp Notification Service REST API Documentation",
                description = "Whatsapp Notification Service microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Szymon Tracz",
                        email = "szymontracz1@gmail.com",
                        url = "https://www.github.com/szopszop"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://www.github.com/szopszop"
                )
        )
)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
