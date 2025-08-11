package az.qrfood.backend.auth.repository;

import az.qrfood.backend.auth.entity.RefreshToken;
import az.qrfood.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link RefreshToken} entities.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find a refresh token by its token value.
     *
     * @param token The token value to search for.
     * @return An Optional containing the found RefreshToken, or empty if not found.
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find a refresh token by its associated user.
     *
     * @param user The user associated with the refresh token.
     * @return An Optional containing the found RefreshToken, or empty if not found.
     */
    Optional<RefreshToken> findByUser(User user);
    
    /**
     * Delete all refresh tokens associated with a specific user.
     *
     * @param user The user whose refresh tokens should be deleted.
     * @return The number of tokens deleted.
     */
    @Modifying
    int deleteByUser(User user);
}