package tracz.authserver.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthServiceImpl;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<AuthUserDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authServiceImpl.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authServiceImpl.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authServiceImpl.refreshToken(request));
    }
}
