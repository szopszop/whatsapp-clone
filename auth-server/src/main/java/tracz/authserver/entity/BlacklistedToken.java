package tracz.authserver.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "token_blacklist")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jwtId;

    @Column(nullable = false)
    private Instant expiryDate;
}
