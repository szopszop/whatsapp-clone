package tracz.authserver.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthUserService;

@RestController
@RequestMapping(ApiPaths.API_AUTH)
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserDTO> register(@RequestBody RegisterRequest request) {
        AuthUserDTO authUserDTO = authUserService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format("/api/v1/auth/%s", authUserDTO.getId()));
        return new ResponseEntity<>(authUserDTO, headers, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authUserService.authenticate(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authUserService.refreshToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
