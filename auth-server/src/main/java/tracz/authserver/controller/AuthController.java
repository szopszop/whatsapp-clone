package tracz.authserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.service.AuthUserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "login-page";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if(!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest("", ""));
        }
        return "register-page";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }

        try {
            authUserService.register(request);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("registrationError", "User with this email already exists");
            return "redirect:/register";
        }
        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }
}
