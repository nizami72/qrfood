package az.qrfood.backend.schedulers;

import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.mail.EventPublisherHelper;
import az.qrfood.backend.mail.EventTypeResolver;
import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.dto.SubscriptionType;
import az.qrfood.backend.mail.repository.EmailTemplateRepository;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingScheduler {

    //<editor-fold desc="Fields">
    private final EateryRepository eateryRepository;
    private final EmailTemplateRepository templateRepository;
    private final EventPublisherHelper eventPublisherHelper;

    @Value("${app.scheduler.onboarding.delay}")
    private Duration delay;
    private final ApplicationEventPublisher eventPublisher;
    //</editor-fold>

    /**
     * Will run as per cron.
     */
//    @Scheduled(cron = "0 45 10 * * *")
    @Scheduled(fixedRate = 20000)
    @Transactional
    public void checkOnboardingStatus() {
        // Find users whose last login was more than 'delayHours' ago
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(delay.toHours());
        Arrays.stream(OnboardingStatus.values())
                .forEach(status -> {

                    EventType eventType = EventTypeResolver.resolveByOnboardingStatus(status);

                    List<Eatery> stuckUsers = eateryRepository.findEateriesForOnboardingNotification(
                            status,
                            SubscriptionType.ONBOARDING_NUDGE.getValue(),
                            eventType,
                            cutoffTime
                    );

                    if (stuckUsers.isEmpty()) {
//                        log.debug("No users to notify for status: [{}]", status);
                        return;
                    }

                    for (Eatery eatery : stuckUsers) {
                        try {
                            processSingleUser(eatery);
                        } catch (Exception e) {
                            log.error("Unable to notify user [{}] for eatery [{}]", eatery.getUserProfiles().getFirst().getUser().getUsername(), eatery.getId(), e);
                        }
                    }

                });
    }

    private void processSingleUser(Eatery eatery) {
        UserProfile profile = eatery.getUserProfiles().stream()
                .filter(up -> up.getUser().getRoles().contains(Role.EATERY_ADMIN))
                .findFirst().orElseThrow();

        EventType eventType = EventTypeResolver.resolveByOnboardingStatus(eatery.getOnboardingStatus());

        eventPublisherHelper.publishEmailEvent(profile.getUser(), eventType);




//        EmailTemplate template = templateRepository.findByEventType(eventType)
//                .orElseThrow(() -> new RuntimeException("Template not found: " + eventType));

//        eventPublisher.publishEvent(new EmailEvent(mail, locale, template, eventType, Map.of("adminName", name,
//                "magicLinkUrl", link,
//                "unsubscribeUrl", "https//google.com"));
    }

}