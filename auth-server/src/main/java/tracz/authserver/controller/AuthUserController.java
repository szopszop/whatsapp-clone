package tracz.authserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthUserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<AuthUserDTO> register(@RequestBody RegisterRequest request) {
        log.info("Register request: {}", request);
        AuthUserDTO authUserDTO = authUserService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, ApiPaths.REGISTER + "/" + authUserDTO.getId());
        return new ResponseEntity<>(authUserDTO, headers, HttpStatus.CREATED);
    }

    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authUserService.authenticate(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping(ApiPaths.REFRESH)
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authUserService.refreshToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
