package tracz.userservice.service;

import java.util.List;
import java.util.UUID;
import tracz.userservice.dto.ContactResponseDTO;
import tracz.userservice.dto.ContactRequestDTO;

public interface ContactService {
    
    /**
     * Adds a user as a contact for the current user.
     * 
     * @param userId The ID of the current user.
     * @param contactRequest The request containing the contact's ID.
     * @return The created contact.
     */
    ContactResponseDTO addContact(UUID userId, ContactRequestDTO contactRequest);
    
    /**
     * Gets all contacts for a user.
     * 
     * @param userId The ID of the user.
     * @return A list of contacts.
     */
    List<ContactResponseDTO> getUserContacts(UUID userId);
    
    /**
     * Checks if a user is already a contact of the current user.
     * 
     * @param userId The ID of the current user.
     * @param contactId The ID of the potential contact.
     * @return True if the user is already a contact, false otherwise.
     */
    boolean isUserContact(UUID userId, UUID contactId);
    
    /**
     * Gets a specific contact of a user.
     * 
     * @param userId The ID of the user.
     * @param contactId The ID of the contact.
     * @return The contact if found.
     */
    ContactResponseDTO getUserContact(UUID userId, UUID contactId);
}