package tracz.userservice.entity;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tracz.commonservice.config.validation.Email;
import tracz.commonservice.config.validation.Password;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @UuidGenerator
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(unique = true, nullable = false, length = 100)
    @Size(max = 100)
    @Email
    private String email;

    @Password
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant updateDate;
}