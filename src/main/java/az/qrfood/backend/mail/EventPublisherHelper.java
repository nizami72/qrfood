package az.qrfood.backend.mail;

import az.qrfood.backend.auth.service.AuthTokenService;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.dto.events.EmailEvent;
import az.qrfood.backend.mail.entity.EmailTemplate;
import az.qrfood.backend.mail.repository.EmailTemplateRepository;
import az.qrfood.backend.mail.service.NotificationLogService;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
public class EventPublisherHelper {

    @Value("${host.name.redirect}")
    private String frontendBaseUrl;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationLogService logService;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateRepository templateRepository;
    private final AuthTokenService authTokenService;
    private static final Map<String, String> GREETINGS = Map.of(
            "az", "Hörmətli müştəri",
            "ru", "Уважаемый клиент",
            "en", "Dear customer"
    );

    public EventPublisherHelper(ApplicationEventPublisher eventPublisher, NotificationLogService logService, EmailTemplateRepository emailTemplateRepository, EmailTemplateRepository templateRepository, AuthTokenService authTokenService) {
        this.eventPublisher = eventPublisher;
        this.logService = logService;
        this.emailTemplateRepository = emailTemplateRepository;
        this.templateRepository = templateRepository;
        this.authTokenService = authTokenService;
    }


    public void publishEmailEvent(User user, EventType eventType) {
        String locale = Optional.ofNullable(user.getProfile())
                .map(UserProfile::getLocale)
                .orElse("az");

        String name = GREETINGS.getOrDefault(locale, GREETINGS.get("az"));

        String link = String.format("%s/auth/verify?token=%s", frontendBaseUrl, authTokenService.createMagicLinkToken(user));
        Optional<EmailTemplate> templateOp = emailTemplateRepository.findByEventType(eventType);
        if(templateOp.isEmpty()) {
            String err = "Template not found for event type";
            logService.logFailure(user.getUsername(), eventType, locale, err);
            log.error("Unable to find template for event type [{}]",eventType);
            return;
        }

        EmailEvent emailEvent = new EmailEvent(
                user.getUsername(),
                locale,
                templateOp.get(),
                eventType,
                Map.of("adminName", name,
                        "magicLinkUrl", link,
                        "unsubscribeUrl", "https//google.com"));
        eventPublisher.publishEvent(emailEvent);
    }

    @Transactional
    public String createMicLinkAndPublishEvent(User user, String name, String email, String ipAddress,
                                               String userAgent, String locale) {
        String link = String.format("%s/auth/verify?token=%s", frontendBaseUrl, authTokenService.createMagicLinkToken(user));
        log.info("Magic link generated for [{}]", email);
        log.info("Magic link sent to email [{}]", email);
        EventType eventType = EventType.MAGIC_LINK;
        EmailTemplate template = templateRepository.findByEventType(eventType)
                .orElseThrow(() -> new RuntimeException("Template not found: " + eventType));
        eventPublisher.publishEvent(new EmailEvent(
                email,
                locale,
                template,
                eventType,
                Map.of(
                        "adminName", name,
                        "magicLinkUrl", link,
                        "requestTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                        "deviceInfo", Util.parseDeviceInfo(userAgent),
                        "ipAddress", ipAddress)));
        return link;
    }


}