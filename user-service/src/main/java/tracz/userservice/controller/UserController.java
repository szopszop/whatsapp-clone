package tracz.userservice.controller;

import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.dto.UserResponseDTO;
import tracz.userservice.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.USER_API)
public class UserController {

    private final UserService userService;

    @GetMapping("/id")
    public UserResponseDTO getUserById(@RequestParam("id") UUID id) {
        return userService.findById(id);
    }

    @GetMapping("/by-email")
    public UserResponseDTO getUserByEmail(@RequestParam("email") @Valid String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/exists-by-email")
    public boolean checkEmailExists(@RequestParam("email") @Valid String email) {
        return userService.existsByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public Page<UserResponseDTO> getAllUsers() {
        return userService.getUsers("email", 0, 25);
    }

}
