package az.qrfood.backend.log.controller;

import az.qrfood.backend.constant.ApiRoutes;
import az.qrfood.backend.log.dto.FrontendLogDTO;
import az.qrfood.backend.log.service.FrontendLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Frontend Log Management", description = "API endpoints for receiving frontend logs")
public class FrontendLogController {

    private final FrontendLogService frontendLogService;

    /**
     * Endpoint for receiving frontend logs.
     * 
     * @param logDTO The frontend log data
     * @param request The HTTP request
     * @return A response entity indicating success
     */
    @Operation(summary = "Receive frontend log message", description = "Receives log messages from the frontend application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log message received successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid log data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiRoutes.LOGS_FRONTEND)
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