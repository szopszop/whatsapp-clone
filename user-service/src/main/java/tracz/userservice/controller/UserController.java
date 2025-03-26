package tracz.userservice.controller;

import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tracz.userservice.config.ApiPaths;
import tracz.userservice.dto.RegisterRequest;
import tracz.userservice.dto.UserDTO;
import tracz.userservice.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.USER_API)
public class UserController {

    private final UserService userService;

    @GetMapping(ApiPaths.USER_BY_ID)
    public UserDTO getUserById(@PathVariable("id")UUID id) {
        return userService.findById(id);
    }

    @GetMapping("/by-email")
    public UserDTO getUserByEmail(@RequestParam("email") @Valid String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/exists-by-email")
    public boolean checkEmailExists(@RequestParam("email") @Valid String email) {
        return userService.existsByEmail(email);
    }

    @PostMapping()
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterRequest request) {
        UserDTO savedUser = userService.register(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format(ApiPaths.USER_API + "/%s", savedUser.getId()));
        return new ResponseEntity<>(savedUser, headers, HttpStatus.CREATED);
    }
}
