package az.qrfood.backend.log.service;

import az.qrfood.backend.log.dto.FrontendLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for handling frontend logs.
 * This service receives log data from the frontend and logs it using the backend's logging system.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FrontendLogService {

    /**
     * Logs a frontend error message.
     * 
     * @param logDTO The frontend log data transfer object
     */
    public void logFrontendMessage(FrontendLogDTO logDTO) {
        String logMessage = buildLogMessage(logDTO);
        
        switch (logDTO.getLevel().toLowerCase()) {
            case "error":
                log.error(logMessage);
                break;
            case "warn":
                log.warn(logMessage);
                break;
            case "info":
                log.info(logMessage);
                break;
            case "debug":
                log.debug(logMessage);
                break;
            default:
                // Default to info level if an unknown level is provided
                log.info(logMessage);
                break;
        }
    }
    
    /**
     * Builds a formatted log message from the frontend log data.
     * 
     * @param logDTO The frontend log data transfer object
     * @return A formatted log message string
     */
    private String buildLogMessage(FrontendLogDTO logDTO) {
        StringBuilder message = new StringBuilder();
        message.append("[FRONTEND] ");
        
        if (logDTO.getSource() != null && !logDTO.getSource().isEmpty()) {
            message.append("[").append(logDTO.getSource()).append("] ");
        }
        
        message.append(logDTO.getMessage());
        
        if (logDTO.getUserId() != null && !logDTO.getUserId().isEmpty()) {
            message.append(" | User: ").append(logDTO.getUserId());
        }
        
        if (logDTO.getDetails() != null && !logDTO.getDetails().isEmpty()) {
            message.append(" | Details: ").append(logDTO.getDetails());
        }
        
        if (logDTO.getUserAgent() != null && !logDTO.getUserAgent().isEmpty()) {
            message.append(" | UserAgent: ").append(logDTO.getUserAgent());
        }
        
        return message.toString();
    }
}