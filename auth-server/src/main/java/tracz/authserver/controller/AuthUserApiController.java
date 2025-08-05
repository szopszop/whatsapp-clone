package tracz.authserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracz.authserver.dto.*;
import tracz.authserver.dto.AuthUserDTO;
import tracz.authserver.dto.RegisterRequest;
import tracz.authserver.service.AuthUserService;
import java.util.UUID;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthUserApiController {

    private final AuthUserService authUserService;

    @Operation(summary = "Register a new user",
            description = "Creates a new user account in the auth-server.")
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
    @PostMapping("/register")
    public ResponseEntity<?> handleRegistration(@Valid @RequestBody RegisterRequest request) {
        AuthUserDTO registeredUser = authUserService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @DeleteMapping("{authUserId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID authUserId) {
        authUserService.deleteUser(authUserId);
        return ResponseEntity.noContent().build();
    }
}
