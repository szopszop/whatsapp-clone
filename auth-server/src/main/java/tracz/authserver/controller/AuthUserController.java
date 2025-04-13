package tracz.authserver.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthUserService;

@Slf4j
@RestController
@RequestMapping(ApiPaths.API_AUTH)
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserDTO> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Register request: {}", request);
        AuthUserDTO authUserDTO = authUserService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", (ApiPaths.API_AUTH + "/" + authUserDTO.getId()));
        return new ResponseEntity<>(authUserDTO, headers, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody @Valid AuthRequest request) {
        AuthResponse authResponse = authUserService.authenticate(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResponse authResponse = authUserService.refreshToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
