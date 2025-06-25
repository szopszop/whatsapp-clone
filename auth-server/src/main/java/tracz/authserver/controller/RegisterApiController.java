package tracz.authserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.service.AuthUserService;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class RegisterApiController {

    private final AuthUserService authUserService;

    @PostMapping("/register")
    public ResponseEntity<?> handleRegistration(@Valid @RequestBody RegisterRequest request) {
        AuthUserDTO registeredUser = authUserService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}
