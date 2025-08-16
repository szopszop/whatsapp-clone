package tracz.userservice.mapper;

import tracz.userservice.dto.ConnectionResponseDTO;
import tracz.userservice.entity.Connection;
import tracz.userservice.entity.User;

import java.util.Map;
import java.util.UUID;

public class ConnectionMapper {

    /**
     * Convert a Connection entity to a ConnectionResponseDTO
     *
     * @param connection The Connection entity
     * @param userMap Map of user IDs to User entities for getting user names
     * @return The ConnectionResponseDTO
     */
    public static ConnectionResponseDTO connectionToDto(Connection connection, Map<UUID, User> userMap) {
        if (connection == null) {
            return null;
        }

        String requesterName = "";
        String targetName = "";

        if (userMap != null) {
            User requester = userMap.get(connection.getRequesterId());
            User target = userMap.get(connection.getTargetId());

            if (requester != null) {
                requesterName = (requester.getFirstName() != null ? requester.getFirstName() : "") + 
                               " " + 
                               (requester.getLastName() != null ? requester.getLastName() : "");
                requesterName = requesterName.trim();
            }

            if (target != null) {
                targetName = (target.getFirstName() != null ? target.getFirstName() : "") + 
                            " " + 
                            (target.getLastName() != null ? target.getLastName() : "");
                targetName = targetName.trim();
            }
        }

        return new ConnectionResponseDTO(
                connection.getId(),
                connection.getRequesterId(),
                connection.getTargetId(),
                requesterName,
                targetName,
                connection.getStatus().name(),
                connection.getCreatedAt(),
                connection.getUpdatedAt()
        );
    }
}
