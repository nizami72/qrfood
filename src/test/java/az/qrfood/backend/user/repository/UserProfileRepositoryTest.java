package az.qrfood.backend.user.repository;

import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.entity.NotificationLog;
import az.qrfood.backend.mail.repository.NotificationLogRepository;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.mock.mockito.MockBean(az.qrfood.backend.user.service.UserService.class)
@org.springframework.boot.test.mock.mockito.MockBean(org.springframework.security.crypto.password.PasswordEncoder.class)
class UserProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    void testFindEateryAdminsWithoutEateryAndNotNotified() {
        // Given
        User user1 = new User();
        user1.setUsername("admin1@example.com");
        user1.setPassword("password");
        user1.setRoles(new HashSet<>(Set.of(Role.EATERY_ADMIN)));
        entityManager.persist(user1);

        UserProfile profile1 = new UserProfile();
        profile1.setUser(user1);
        profile1.setName("Admin One");
        profile1.setEmailSubscription(16); // ONBOARDING_NUDGE
        profile1.setCreated(LocalDateTime.now().minusHours(25));
        profile1.setUpdated(LocalDateTime.now().minusHours(25));
        entityManager.persist(profile1);

        // User 2: has eatery (should NOT be found)
        // (Skipping for now or will add later)

        // User 3: already notified (should NOT be found)
        User user3 = new User();
        user3.setUsername("admin3@example.com");
        user3.setPassword("password");
        user3.setRoles(new HashSet<>(Set.of(Role.EATERY_ADMIN)));
        entityManager.persist(user3);

        UserProfile profile3 = new UserProfile();
        profile3.setUser(user3);
        profile3.setName("Admin Three");
        profile3.setEmailSubscription(16);
        profile3.setCreated(LocalDateTime.now().minusHours(25));
        profile3.setUpdated(LocalDateTime.now().minusHours(25));
        entityManager.persist(profile3);

        NotificationLog log3 = new NotificationLog();
        log3.setUserEmail("admin3@example.com");
        log3.setEventType(EventType.CREATE_EATERY);
        entityManager.persist(log3);

        // User 4: different subscription (should NOT be found)
        User user4 = new User();
        user4.setUsername("admin4@example.com");
        user4.setPassword("password");
        user4.setRoles(new HashSet<>(Set.of(Role.EATERY_ADMIN)));
        entityManager.persist(user4);

        UserProfile profile4 = new UserProfile();
        profile4.setUser(user4);
        profile4.setName("Admin Four");
        profile4.setEmailSubscription(1); 
        profile4.setCreated(LocalDateTime.now().minusHours(25));
        profile4.setUpdated(LocalDateTime.now().minusHours(25));
        entityManager.persist(profile4);

        // User 5: multiple subscriptions including 16 (should be found)
        User user5 = new User();
        user5.setUsername("admin5@example.com");
        user5.setPassword("password");
        user5.setRoles(new HashSet<>(Set.of(Role.EATERY_ADMIN)));
        entityManager.persist(user5);

        UserProfile profile5 = new UserProfile();
        profile5.setUser(user5);
        profile5.setName("Admin Five");
        profile5.setEmailSubscription(17); // 1 + 16
        profile5.setCreated(LocalDateTime.now().minusHours(25));
        profile5.setUpdated(LocalDateTime.now().minusHours(25));
        entityManager.persist(profile5);

        // User 6: too recent (should NOT be found)
        User user6 = new User();
        user6.setUsername("admin6@example.com");
        user6.setPassword("password");
        user6.setRoles(new HashSet<>(Set.of(Role.EATERY_ADMIN)));
        entityManager.persist(user6);

        UserProfile profile6 = new UserProfile();
        profile6.setUser(user6);
        profile6.setName("Admin Six");
        profile6.setEmailSubscription(16);
        profile6.setCreated(LocalDateTime.now());
        profile6.setUpdated(LocalDateTime.now());
        entityManager.persist(profile6);

        entityManager.flush();

        // When
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<UserProfile> result = userProfileRepository.findEateryAdminsWithoutEateryAndNotNotified(cutoff);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(up -> up.getUser().getUsername())
                .containsExactlyInAnyOrder("admin1@example.com", "admin5@example.com");
    }
}
