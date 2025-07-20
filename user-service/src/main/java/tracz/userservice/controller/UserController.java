package tracz.userservice.controller;

import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.dto.*;
import tracz.userservice.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.USER_API)
public class UserController {

    private final UserService userService;

    @GetMapping("/id")
    public UserResponseDTO getUserByAuthServerUserId(@RequestParam("id") UUID id) {
        return userService.findByAuthServerUserId(id);
    }

    @GetMapping("/by-email")
    public UserResponseDTO getUserByEmail(@RequestParam("email") @Valid String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/exists-by-email")
    public boolean checkEmailExists(@RequestParam("email") @Valid String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID authUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userService.findByAuthServerUserId(authUserId));
    }

    @PutMapping("/me/status")
    public ResponseEntity<Void> updateMyStatus(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserStatusUpdateDTO statusUpdateDTO) {
        UUID authUserId = UUID.fromString(jwt.getSubject());
        userService.updateUserStatus(authUserId, statusUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserProfileUpdateDTO updateDTO) {
        UUID authUserId = UUID.fromString(jwt.getSubject());
        UserResponseDTO updatedUser = userService.updateMyProfile(authUserId, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.searchUsers(query, page, size));
    }
}
