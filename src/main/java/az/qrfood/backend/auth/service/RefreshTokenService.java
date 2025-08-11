package az.qrfood.backend.auth.service;

import az.qrfood.backend.auth.entity.RefreshToken;
import az.qrfood.backend.auth.exception.TokenRefreshException;
import az.qrfood.backend.auth.repository.RefreshTokenRepository;
import az.qrfood.backend.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing refresh tokens.
 * <p>
 * This service handles the creation, validation, and deletion of refresh tokens.
 * </p>
 */
@Service
public class RefreshTokenService {

    /**
     * The expiration time for refresh tokens in milliseconds.
     * Default is 7 days.
     */
    @Value("${jwt.refresh.expiration:604800000}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Constructs the RefreshTokenService with necessary dependencies.
     *
     * @param refreshTokenRepository The repository for accessing refresh token data.
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Finds a refresh token by its token value.
     *
     * @param token The token value to search for.
     * @return An Optional containing the found RefreshToken, or empty if not found.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Creates a new refresh token for a user.
     * <p>
     * If a refresh token already exists for the user, it will be replaced.
     * </p>
     *
     * @param user     The user for whom to create the refresh token.
     * @param eateryId The ID of the eatery associated with this token, if applicable.
     * @return The created RefreshToken.
     */
    public RefreshToken createRefreshToken(User user) {
        // Check if a user already has a refresh token
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        // If exists, delete it
        existingToken.ifPresent(refreshTokenRepository::delete);

        // Create a new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verifies if a refresh token is valid (exists and not expired).
     *
     * @param token The refresh token to verify.
     * @return The verified RefreshToken.
     * @throws TokenRefreshException If the token is invalid or expired.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }

        return token;
    }

    /**
     * Deletes all refresh tokens for a specific user.
     *
     * @param user The user whose refresh tokens should be deleted.
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
