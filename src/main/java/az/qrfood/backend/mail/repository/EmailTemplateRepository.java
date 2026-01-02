package az.qrfood.backend.mail.repository;

import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByEventType(EventType eventType);

}