package az.qrfood.backend.schedulers;

import az.qrfood.backend.mail.EventPublisherHelper;
import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class EateryAdminScheduler {

    private final UserProfileRepository userProfileRepository;
    private final EventPublisherHelper eventPublisherHelper;

    @Value("${app.scheduler.eatery-admin.delay}")
    private Duration delay;

    /**
     * Will run as per cron.
     */
//    @Scheduled(cron = "0 45 10 * * *")
    @Scheduled(fixedRate = 20000)
    @Transactional
    public void checkEateryMissing() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(delay.toMinutes());
        List<UserProfile> stuckUsers = userProfileRepository.findEateryAdminsWithoutEateryAndNotNotified(cutoff);
        stuckUsers.forEach(this::processSingleUser);
    }

    private void processSingleUser(UserProfile profile) {
        eventPublisherHelper.publishEmailEvent(profile.getUser(), EventType.CREATE_EATERY);
    }


}