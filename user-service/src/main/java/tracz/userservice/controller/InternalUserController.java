package tracz.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.config.InternalApiPaths;
import tracz.userservice.dto.UserCreationRequestDTO;
import tracz.userservice.dto.UserResponseDTO;
import tracz.userservice.service.UserService;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(InternalApiPaths.USERS)
public class InternalUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> provisionUser(@Valid @RequestBody UserCreationRequestDTO request) {
        log.info("Received request from auth-server for {}, authId: {}", request.email(), request.authUserId());
        UserResponseDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

}
