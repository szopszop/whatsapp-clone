package tracz.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID authServerUserId;

    @Column(unique = true, nullable = false, length = 100)
    @Size(max = 100)
    private String email;

    @ElementCollection()
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private String firstName;
    private String lastName;
    @Size(max = 255)
    private String profilePictureUrl;
    @Size(max = 500)
    private String about;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.OFFLINE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_fcm_tokens", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "fcm_token")
    private Set<String> fcmTokens = new HashSet<>();

}