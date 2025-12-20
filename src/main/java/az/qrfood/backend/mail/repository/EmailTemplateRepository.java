package az.qrfood.backend.mail.repository;

import az.qrfood.backend.mail.entity.EmailTemplate;
import az.qrfood.backend.mail.service.TemplateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByTemplateKey(TemplateKey templateKey);

}