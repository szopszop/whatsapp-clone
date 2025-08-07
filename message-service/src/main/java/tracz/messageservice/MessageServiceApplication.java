package tracz.messageservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableMongoAuditing
@OpenAPIDefinition(
        info = @Info(
                title = "Whatsapp Message Service REST API Documentation",
                description = "Whatsapp Message Service microservice REST API Documentation",
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
public class MessageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }

}
