package az.qrfood.backend.user.profile;

import az.qrfood.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserProfile entity.
 * Provides methods to interact with the user_profiles table in the database.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Find a user profile by the associated user.
     *
     * @param user The user entity
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByUser(User user);
    
    /**
     * Check if a user profile exists for the given user.
     *
     * @param user The user entity
     * @return true if a profile exists, false otherwise
     */
    boolean existsByUser(User user);
}