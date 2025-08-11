package tracz.userservice.mapper;

import tracz.userservice.dto.ContactResponseDTO;
import tracz.userservice.entity.Contact;

public class ContactMapper {

    public static ContactResponseDTO toDto(Contact contact) {
        if (contact == null) {
            return null;
        }
        
        return new ContactResponseDTO(
            contact.getId(),
            UserMapper.userToDto(contact.getContact()),
            contact.getConversationId()
        );
    }
}