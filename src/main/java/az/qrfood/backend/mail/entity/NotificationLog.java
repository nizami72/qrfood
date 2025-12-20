package az.qrfood.backend.mail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String templateKey;
    private String localeUsed;
    @Enumerated(EnumType.STRING)
    private NotifStatus status;
    private String errorMessage;
    private LocalDateTime sentAt = LocalDateTime.now();
}