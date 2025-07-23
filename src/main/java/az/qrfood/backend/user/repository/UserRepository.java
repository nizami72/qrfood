package az.qrfood.backend.user.repository;

import az.qrfood.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * <p>
 * This interface provides standard CRUD (Create, Read, Update, Delete)
 * operations for {@link User} entities and supports custom query methods
 * for retrieving users by their username.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a user by their username (login).
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the user if found, or empty if not found.
     */
    Optional<User> findByUsername(String username);

    Optional<User> deleteUserByUsername(String username);
}
