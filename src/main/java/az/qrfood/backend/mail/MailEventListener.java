package az.qrfood.backend.mail;

import az.qrfood.backend.mail.dto.events.EmailEvent;
import az.qrfood.backend.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailEventListener {

    private final EmailService emailService;

    @Async("emailExecutor")
    @EventListener
    public void handleEmailSending(EmailEvent event) {
        log.debug("Processing email event: [{}] for [{}]", event.event(), event.email());
        emailService.sendEmailI18n(event);
    }
}