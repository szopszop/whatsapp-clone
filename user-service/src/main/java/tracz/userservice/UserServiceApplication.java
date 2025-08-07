package tracz.userservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableFeignClients
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Whatsapp User Service REST API Documentation",
                description = "Whatsapp User Service microservice REST API Documentation",
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
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
