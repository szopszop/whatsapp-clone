package tracz.authserver.entity;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import tracz.authserver.config.validation.Email;
import tracz.authserver.config.validation.Password;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "auth_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class AuthUser {
    @Id
    @UuidGenerator
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Password
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
}
