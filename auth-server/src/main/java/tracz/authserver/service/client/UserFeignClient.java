package tracz.authserver.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PostMapping("/register")
    void register();

}
