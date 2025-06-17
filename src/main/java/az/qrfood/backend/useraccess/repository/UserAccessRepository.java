package az.qrfood.backend.useraccess.repository;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.useraccess.entity.UserAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing UserAccess entities.
 */
@Repository
public interface UserAccessRepository extends JpaRepository<UserAccess, Long> {

    /**
     * Find all user access records for a specific user.
     *
     * @param user the user
     * @return list of user access records
     */
    List<UserAccess> findByUser(User user);

    /**
     * Find all user access records for a specific eatery.
     *
     * @param eatery the eatery
     * @return list of user access records
     */
    List<UserAccess> findByEatery(Eatery eatery);

    /**
     * Find all user access records with a specific role.
     *
     * @param role the role
     * @return list of user access records
     */
    List<UserAccess> findByRole(Role role);

    /**
     * Find a user access record for a specific user and eatery.
     *
     * @param user the user
     * @param eatery the eatery
     * @return optional user access record
     */
    Optional<UserAccess> findByUserAndEatery(User user, Eatery eatery);

    /**
     * Find all user access records for a specific user and role.
     *
     * @param user the user
     * @param role the role
     * @return list of user access records
     */
    List<UserAccess> findByUserAndRole(User user, Role role);

    /**
     * Find all user access records for a specific eatery and role.
     *
     * @param eatery the eatery
     * @param role the role
     * @return list of user access records
     */
    List<UserAccess> findByEateryAndRole(Eatery eatery, Role role);

    /**
     * Find a user access record for a specific user, eatery, and role.
     *
     * @param user the user
     * @param eatery the eatery
     * @param role the role
     * @return optional user access record
     */
    Optional<UserAccess> findByUserAndEateryAndRole(User user, Eatery eatery, Role role);
}