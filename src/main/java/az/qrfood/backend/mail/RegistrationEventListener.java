package az.qrfood.backend.mail;

import az.qrfood.backend.mail.dto.UserRegisteredEvent;
import az.qrfood.backend.mail.service.EmailService;
import az.qrfood.backend.mail.service.TemplateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationEventListener {

    private final EmailService emailService;

    @Async("emailExecutor")
    @EventListener
    public void handleMagicLinkLoginOrRegistration(UserRegisteredEvent event) {
        log.info("Got magic link event for [{}]", event.email());
        emailService.sendEmailI18n(event.email(), TemplateKey.REGISTRATION, event.locale(), event.map());
    }
}