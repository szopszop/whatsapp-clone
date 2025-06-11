package tracz.authserver.controller;


import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.config.ApiPaths;
import tracz.authserver.dto.*;
import tracz.authserver.service.AuthUserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthUserController {
    private final AuthUserService authUserService;
    private final Environment environment;

    @Value("${info.app.version}")
    private String buildVersion;


    @Operation(summary = "Register a new user",
            description = "Creates a new user account in the auth-server and provisions it in the user-service.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = AuthUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "User with this email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<AuthUserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request with email: {}", request.getEmail());
        AuthUserDTO authUserDTO = authUserService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, ApiPaths.REGISTER + "/" + authUserDTO.getId());
        return new ResponseEntity<>(authUserDTO, headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "User authentication",
            description = "Login a new User in the auth-server"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    }
    )
    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        AuthResponse authResponse = authUserService.authenticate(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Token refresh operation",
            description = "Generates a new access token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Token generated successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    }
    )
    @PostMapping(ApiPaths.REFRESH)
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authUserService.refreshToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    @Operation(
            summary = "Get Build information", description = "Get Auth Server's Build information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Build information sent",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    }
    )
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        log.debug("getBuildInfo() method Invoked");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @Operation(
            summary = "Get Java version", description = "Get Java version"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Java version sent",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    }
    )
    @RateLimiter(name = "getJavaVersion")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }
}
