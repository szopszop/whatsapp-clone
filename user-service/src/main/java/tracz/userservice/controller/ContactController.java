package tracz.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tracz.userservice.dto.ContactRequestDTO;
import tracz.userservice.dto.ContactResponseDTO;
import tracz.userservice.exception.BadRequestException;
import tracz.userservice.service.ContactService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDTO> addContact(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ContactRequestDTO contactRequest) {
        UUID userId = getAuthUserIdFromJwt(jwt);
        ContactResponseDTO contact = contactService.addContact(userId, contactRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDTO>> getUserContacts(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = getAuthUserIdFromJwt(jwt);
        List<ContactResponseDTO> contacts = contactService.getUserContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponseDTO> getUserContact(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID contactId) {
        UUID userId = getAuthUserIdFromJwt(jwt);
        ContactResponseDTO contact = contactService.getUserContact(userId, contactId);
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/check/{contactId}")
    public ResponseEntity<Boolean> checkIfUserIsContact(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID contactId) {
        UUID userId = getAuthUserIdFromJwt(jwt);
        boolean isContact = contactService.isUserContact(userId, contactId);
        return ResponseEntity.ok(isContact);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID contactId) {
        UUID userId = getAuthUserIdFromJwt(jwt);
        contactService.deleteContact(userId, contactId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the user's UUID from the JWT subject claim.
     *
     * @param jwt The JWT token from the authentication principal.
     * @return The user's UUID.
     * @throws BadRequestException if the subject claim is null or not a valid UUID.
     */
    private UUID getAuthUserIdFromJwt(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Could not parse user ID from JWT subject: '{}'", jwt.getSubject(), e);
            throw new BadRequestException("Invalid user identifier in token.");
        }
    }
}