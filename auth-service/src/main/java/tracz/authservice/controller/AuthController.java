package tracz.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tracz.authservice.config.ApiPaths;
import tracz.authservice.dto.*;
import tracz.authservice.service.AuthService;
import tracz.commonservice.dto.UserDTO;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.AUTH_API)
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterRequest request) {
        UserDTO savedUser = authService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format(ApiPaths.AUTH_API + "/%s", savedUser.getId()));
        return new ResponseEntity<>(savedUser, headers, HttpStatus.CREATED);
    }

    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(ApiPaths.REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }


}
