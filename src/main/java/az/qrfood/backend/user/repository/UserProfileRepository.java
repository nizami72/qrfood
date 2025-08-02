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
 * Spring Data JPA repository for the {@link UserProfile} entity.
 * <p>
 * This interface provides methods to interact with the {@code user_profiles} table in the database,
 * allowing for CRUD operations and custom queries related to user profiles.
 * </p>
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Retrieves a user profile by the associated {@link User} entity.
     *
     * @param user The {@link User} entity for which to find the profile.
     * @return An {@link Optional} containing the user profile if found, or empty if not found.
     */
    Optional<UserProfile> findByUser(User user);

    /**
     * Checks if a user profile exists for the given {@link User} entity.
     *
     * @param user The {@link User} entity to check for an associated profile.
     * @return {@code true} if a profile exists for the user, {@code false} otherwise.
     */
    boolean existsByUser(User user);

    /**
     * Retrieves all user profiles that are associated with the specified restaurant ID.
     * <p>
     * This method uses a custom JPQL query to search for user profiles that have a relationship
     * with the eatery identified by the given ID.
     * </p>
     *
     * @param restaurantId The ID of the restaurant to search for within user profiles.
     * @return A {@link List} of {@link UserProfile} entities associated with the given restaurant ID.
     */
    @Query("SELECT up FROM UserProfile up JOIN up.eateries e WHERE e.id = :restaurantId")
    List<UserProfile> findByRestaurantId(@Param("restaurantId") Long restaurantId);
}
