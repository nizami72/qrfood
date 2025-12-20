package az.qrfood.backend.mail.service;

import az.qrfood.backend.mail.entity.EmailTemplate;
import az.qrfood.backend.mail.repository.EmailTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Email service.
 */
@Slf4j
@Service
public class EmailService {

    private final EmailTemplateRepository templateRepository;
    private final NotificationLogService logService;
    private final TemplateRenderer renderer;
    private final SmtpMailTransport transport;

    public EmailService(EmailTemplateRepository templateRepository,
                        NotificationLogService logService,
                        TemplateRenderer renderer,
                        SmtpMailTransport transport) {
        this.templateRepository = templateRepository;
        this.logService = logService;
        this.renderer = renderer;
        this.transport = transport;
    }

    /**
     * Executes the email sending process based on a template.
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Searches for a template by key and locale. If the template for the specified locale is not found,
     * it falls back to the default locale template ({@code DEFAULT_LOCALE}).</li>
     * <li>Prepares the context and substitutes variables into the email body and subject.</li>
     * <li>Renders the HTML.</li>
     * <li>Sends the email.</li>
     * <li>Saves the operation result (Success/Failure) to the event log.</li>
     * </ol>
     * <p>
     * If any errors occur during the process (missing template, rendering error, SMTP failure),
     * the exception is caught, logged, and the status is saved as {@code FAILED} in the history.
     *
     * @param to          The recipient's email address.
     */
    public void sendEmailI18n(String to, TemplateKey key, String lang, Map<String, Object> vars) {
        try {
            // 1. Fetch
            EmailTemplate template = templateRepository.findByTemplateKey(key)
                    .orElseThrow(() -> new RuntimeException("Template not found: " + key));

            // 2. Render (Delegated to our new class)
            String finalHtml = renderer.render(template, lang, vars);
            String subject = renderer.renderSubject(template, lang, vars);

            // 3. Send it (Delegated to transport)
            transport.send(to, subject, finalHtml);

            // 4. Log Success
            logService.logSuccess(to, key, lang);

        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            logService.logFailure(to, key, lang, e.getMessage());
        }
    }


}