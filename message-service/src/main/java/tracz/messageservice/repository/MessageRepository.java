package tracz.messageservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tracz.messageservice.entity.Message;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

}
