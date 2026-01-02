package az.qrfood.backend.mail.entity;

import az.qrfood.backend.mail.dto.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "email_templates")
@Data
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "event")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String subjectTemplate;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String bodyHtml;

}