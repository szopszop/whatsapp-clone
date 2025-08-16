package tracz.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracz.userservice.dto.ContactRequestDTO;
import tracz.userservice.dto.ContactResponseDTO;
import tracz.userservice.entity.Contact;
import tracz.userservice.entity.User;
import tracz.userservice.exception.BadRequestException;
import tracz.userservice.exception.ResourceNotFoundException;
import tracz.userservice.mapper.ContactMapper;
import tracz.userservice.repository.ContactRepository;
import tracz.userservice.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ContactResponseDTO addContact(UUID userId, ContactRequestDTO contactRequest) {
        // Validate that the user is not trying to add themselves as a contact
        if (userId.equals(contactRequest.contactId())) {
            throw new BadRequestException("You cannot add yourself as a contact");
        }

        // Check if the contact already exists
        if (isUserContact(userId, contactRequest.contactId())) {
            throw new BadRequestException("This user is already in your contacts");
        }

        // Find the user and the contact
        User user = findUserByAuthIdOrThrow(userId);
        User contactUser = findUserByAuthIdOrThrow(contactRequest.contactId());

        // Create a new conversation ID for this contact
        UUID conversationId = UUID.randomUUID();

        // Create and save the contact
        Contact contact = Contact.builder()
                .user(user)
                .contact(contactUser)
                .conversationId(conversationId)
                .build();

        Contact savedContact = contactRepository.save(contact);
        log.info("Added contact {} for user {}", contactRequest.contactId(), userId);

        return ContactMapper.toDto(savedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDTO> getUserContacts(UUID userId) {
        List<Contact> contacts = contactRepository.findByUserAuthServerUserId(userId);
        return contacts.stream()
                .map(ContactMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserContact(UUID userId, UUID contactId) {
        return contactRepository.existsByUserAuthServerUserIdAndContactAuthServerUserId(userId, contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponseDTO getUserContact(UUID userId, UUID contactId) {
        Contact contact = contactRepository.findByUserAuthServerUserIdAndContactAuthServerUserId(userId, contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        return ContactMapper.toDto(contact);
    }

    private User findUserByAuthIdOrThrow(UUID authId) {
        return userRepository.findByAuthServerUserId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with authId: " + authId));
    }
}