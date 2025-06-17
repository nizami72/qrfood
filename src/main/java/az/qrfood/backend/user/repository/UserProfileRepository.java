package az.qrfood.backend.user.repository;

import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Find all user profiles that have the specified restaurant ID in their restaurantIds list.
     *
     * @param restaurantId The restaurant ID to search for
     * @return List of user profiles associated with the restaurant
     */
    @Query("SELECT up FROM UserProfile up JOIN up.restaurantIds rid WHERE rid = :restaurantId")
    List<UserProfile> findByRestaurantId(@Param("restaurantId") Long restaurantId);
}
