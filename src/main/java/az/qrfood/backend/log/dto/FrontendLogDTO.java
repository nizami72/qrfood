package az.qrfood.backend.log.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for frontend logs.
 * This class represents the structure of log data sent from the frontend to be stored in the backend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendLogDTO {
    
    /**
     * The log level (e.g., "error", "warn", "info", "debug")
     */
    private String level;
    
    /**
     * The error message or log content
     */
    private String message;
    
    /**
     * Additional details about the error or log event (e.g., stack trace)
     */
    private String details;
    
    /**
     * The source of the log (e.g., component name, page URL)
     */
    private String source;
    
    /**
     * User information (if available)
     */
    private String userId;
    
    /**
     * Browser and OS information
     */
    private String userAgent;
    
    /**
     * Timestamp when the log was created on the frontend
     */
    private LocalDateTime timestamp;
}