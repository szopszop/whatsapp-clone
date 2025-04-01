package tracz.userservice.controller;

import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.USER_API)
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(ApiPaths.USER_BY_ID)
    public UserDTO getUserById(@PathVariable("id") UUID id) {
        return userService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-email")
    public UserDTO getUserByEmail(@RequestParam("email") @Valid String email) {
        return userService.findByEmail(email);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/exists-by-email")
    public boolean checkEmailExists(@RequestParam("email") @Valid String email) {
        return userService.existsByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UserDTO> getAllUsers() {
        return userService.getUsers("email", 0, 25);
    }

}
