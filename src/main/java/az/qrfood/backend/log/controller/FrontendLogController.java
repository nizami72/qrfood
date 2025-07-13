package az.qrfood.backend.log.controller;

import az.qrfood.backend.log.dto.FrontendLogDTO;
import az.qrfood.backend.log.service.FrontendLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Controller for handling frontend logs.
 * Provides endpoints for the frontend to send logs to the backend.
 */
@RestController
@RequestMapping("/api/logs")
@Log4j2
@RequiredArgsConstructor
public class FrontendLogController {

    private final FrontendLogService frontendLogService;

    /**
     * Endpoint for receiving frontend logs.
     * 
     * @param logDTO The frontend log data
     * @param request The HTTP request
     * @return A response entity indicating success
     */
    @PostMapping("/frontend")
    public ResponseEntity<Void> logFrontendMessage(
            @RequestBody FrontendLogDTO logDTO,
            HttpServletRequest request) {
        
        // Set timestamp if not provided
        if (logDTO.getTimestamp() == null) {
            logDTO.setTimestamp(LocalDateTime.now());
        }
        
        // Set user agent if not provided
        if (logDTO.getUserAgent() == null || logDTO.getUserAgent().isEmpty()) {
            logDTO.setUserAgent(request.getHeader("User-Agent"));
        }
        
        // Log the message
        frontendLogService.logFrontendMessage(logDTO);
        
        return ResponseEntity.ok().build();
    }
}