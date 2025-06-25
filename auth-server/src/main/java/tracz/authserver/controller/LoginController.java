package tracz.authserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tracz.authserver.service.AuthUserService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthUserService authUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "login-page";
    }
}
