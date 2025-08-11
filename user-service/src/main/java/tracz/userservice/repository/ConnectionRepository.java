package tracz.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracz.userservice.entity.Connection;
import tracz.userservice.entity.Connection.ConnectionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

    /**
     * Find all connections where the user is either the requester or the target and the status is ACCEPTED
     */
    @Query("SELECT c FROM Connection c WHERE (c.requesterId = :userId OR c.targetId = :userId) AND c.status = 'ACCEPTED'")
    List<Connection> findAllAcceptedConnectionsByUserId(@Param("userId") UUID userId);

    /**
     * Find all pending connection requests sent to the user
     */
    List<Connection> findByTargetIdAndStatus(UUID targetId, ConnectionStatus status);

    /**
     * Find all pending connection requests sent by the user
     */
    List<Connection> findByRequesterIdAndStatus(UUID requesterId, ConnectionStatus status);

    /**
     * Find a connection request by requester and target
     */
    Optional<Connection> findByRequesterIdAndTargetId(UUID requesterId, UUID targetId);

    /**
     * Find a connection by id and where the user is either the requester or the target
     */
    Optional<Connection> findByIdAndRequesterIdOrIdAndTargetId(UUID id, UUID requesterId, UUID id2, UUID targetId);
}