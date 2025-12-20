package az.qrfood.backend.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailTransport {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFrom;

    public SmtpMailTransport(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Takes "to", "subject", "htmlBody" and sends it.
    // Knows NOTHING about databases or templates.
    public void send(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = isHtml
        helper.setFrom(mailFrom);
        mailSender.send(message);
    }
}