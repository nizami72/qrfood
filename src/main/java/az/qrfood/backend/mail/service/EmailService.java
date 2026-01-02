package az.qrfood.backend.mail.service;

import az.qrfood.backend.auth.repository.AuthTokenRepository;
import az.qrfood.backend.auth.service.AuthTokenService;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.dto.events.EmailEvent;
import az.qrfood.backend.mail.entity.EmailTemplate;
import az.qrfood.backend.mail.repository.EmailTemplateRepository;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Email service.
 */
@Slf4j
@Service
public class EmailService {

    //<editor-fold desc="Fields">
    private final NotificationLogService logService;
    private final SmtpMailTransport transport;
    private final TemplateRenderer renderer;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public EmailService(NotificationLogService logService,
                        TemplateRenderer renderer,
                        SmtpMailTransport transport) {
        this.logService = logService;
        this.renderer = renderer;
        this.transport = transport;
    }
    //</editor-fold>

    /**
     * Executes the email sending process based on a template.
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Searches for a template by eventType and locale. If the template for the specified locale is not found,
     * it falls back to the default locale template ({@code DEFAULT_LOCALE}).</li>
     * <li>Prepares the context and substitutes variables into the email body and subject.</li>
     * <li>Renders the HTML.</li>
     * <li>Sends the email.</li>
     * <li>Saves the operation result (Success/Failure) to the eventType log.</li>
     * </ol>
     * <p>
     * If any errors occur during the process (missing template, rendering error, SMTP failure),
     * the exception is caught, logged, and the status is saved as {@code FAILED} in the history.
     *
     * @param event All data required to send email.
     */
    public void sendEmailI18n(EmailEvent event) {
        EmailTemplate template = event.template();
        Map<String, Object> map = event.map();
        String lang = event.locale();
        String to = event.email();
        EventType eventType = event.event();

        try {
            // 2. Render (Delegated to our new class)
            String finalHtml = renderer.render(template, lang, map);
            String subject = renderer.renderSubject(template, lang, map);

            // 3. Send it (Delegated to transport)
            transport.send(to, subject, finalHtml);
            logService.logSuccess(to, eventType, lang);

        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            logService.logFailure(to, eventType, lang, e.getMessage());
        }
    }

}