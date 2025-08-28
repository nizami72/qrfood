package az.qrfood.backend.auth.entity;

import az.qrfood.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entity representing a refresh token for JWT authentication.
 * <p>
 * Refresh tokens are used to obtain new access tokens without requiring the user
 * to re-authenticate. They typically have a longer lifespan than access tokens.
 * </p>
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    /**
     * The unique identifier for the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The token value used for authentication.
     */
    @Column(nullable = false, unique = true)
    private String token;
    
    /**
     * The expiration time of the refresh token.
     */
    @Column(nullable = false)
    private Instant expiryDate;

    private Long activeEateryId;

    /**
     * The user associated with this refresh token.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}