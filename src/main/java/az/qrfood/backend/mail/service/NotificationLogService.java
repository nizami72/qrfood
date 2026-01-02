package az.qrfood.backend.mail.service;

import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.entity.NotifStatus;
import az.qrfood.backend.mail.entity.NotificationLog;
import az.qrfood.backend.mail.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository logRepository;

    /**
     * Logs a successful email attempt.
     * Uses REQUIRES_NEW to ensure this log is committed immediately, 
     * independent of any other active transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String to, EventType key, String locale) {
        saveLog(to, key, locale, NotifStatus.SENT, null);
    }

    /**
     * Logs a failed email attempt.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String to, EventType key, String locale, String errorMessage) {
        // Safety: Truncate very long stack traces so they don't crash the DB insert
        String safeError = (errorMessage != null && errorMessage.length() > 2000)
                ? errorMessage.substring(0, 2000) + "..."
                : errorMessage;

        saveLog(to, key, locale, NotifStatus.FAILED, safeError);
    }

    /**
     * Internal helper to construct and save the entity.
     * Wrapped in try-catch because logging failures should NEVER crash the application.
     */
    private void saveLog(String to, EventType key, String locale, NotifStatus status, String error) {
        try {
            NotificationLog logEntry = new NotificationLog();
            logEntry.setUserEmail(to);
            // Assuming your Entity stores the Key as a String. 
            // If it stores the Enum directly, remove .name()
            logEntry.setEventType(key);
            logEntry.setLocaleUsed(locale);
            logEntry.setStatus(status);
            logEntry.setErrorMessage(error);
            logRepository.save(logEntry);
        
        } catch (Exception e) {
            // If the database is down, we can't save the log. 
            // Fallback to file logging so the error isn't lost completely.
            log.error("CRITICAL: Failed to save notification audit log to DB for user {}", to, e);
        }
    }
}