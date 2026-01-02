package az.qrfood.backend.eatery.repository;

import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.mail.dto.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Spring Data JPA repository for the {@link Eatery} entity.
 * <p>
 * This interface provides the standard CRUD (Create, Read, Update, Delete)
 * operations for {@link Eatery} entities, as well as the ability to define
 * custom query methods. Spring Data JPA automatically implements the methods
 * of this interface at runtime.
 * </p>
 */
@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {

    /**
     * Finds eateries that meet all of the following conditions:
     *  - Eatery onboarding status is NOT USER_ADDED
     *  - Eatery is associated with a user who has the EATERY_ADMIN role
     *  - The associated admin's email subscription mask includes the provided subscription bit value
     *  - The associated admin's last login was before the cutoff time
     *  - No notification with the given event has ever been sent to that admin
     *
     * Note: Subscription is stored as a bitmask in UserProfile.emailSubscription.
     *
     * @param status the onboarding status to check
     * @param subscriptionValue the bit value of the subscription to check (see SubscriptionType.getValue())
     * @param event the event identifier used in NotificationLog
     * @param cutoff last login must be before this time
     * @return list of distinct eateries matching the conditions
     */
    @Query("SELECT DISTINCT e " +
            "FROM Eatery e " +
            "JOIN e.userProfiles up " +
            "JOIN up.user u " +
            "WHERE e.onboardingStatus = :status " +
            "AND az.qrfood.backend.user.entity.Role.EATERY_ADMIN MEMBER OF u.roles " +
            "AND MOD(COALESCE(up.emailSubscription, 0) / :subscriptionValue, 2) = 1 " +
            "AND up.lastLogin < :cutoff " +
            "AND NOT EXISTS (SELECT nl FROM NotificationLog nl WHERE nl.userEmail = u.username AND nl.eventType = :event)")
    List<Eatery> findEateriesForOnboardingNotification(
            @Param("status") OnboardingStatus status,
            @Param("subscriptionValue") int subscriptionValue,
            @Param("event") EventType event,
            @Param("cutoff") LocalDateTime cutoff
    );
}
