package az.qrfood.backend.common.exception;

import az.qrfood.backend.auth.exception.TokenException;
import az.qrfood.backend.common.response.ApiResponse;
import az.qrfood.backend.common.response.ResponseCodes;
import az.qrfood.backend.dish.exception.QrFoodDataIntegrityViolation;
import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.user.exception.UserAlreadyExistsException;
import az.qrfood.backend.user.exception.UserExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * <p>
 * This class intercepts exceptions thrown by controllers and services,
 * providing centralized exception handling and returning consistent
 * {@link ApiResponse} error responses to the client.
 * </p>
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * Handles {@link EntityNotFoundException} and returns a 404 Not Found response.
     *
     * @param ex The caught {@link EntityNotFoundException}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the error.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException ex) {
        log.error("Error: [{}]", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Handles {@link MethodArgumentNotValidException} (validation errors) and returns a 400 Bad Request response.
     * It extracts field-specific error messages and includes them in the response.
     *
     * @param ex The caught {@link MethodArgumentNotValidException}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Validation error", errors, 400));
    }


    /**
     * Handles {@link NoResourceFoundException} (e.g., for non-existent API endpoints or static resources)
     * and returns a 404 Not Found response.
     *
     * @param ex The caught {@link NoResourceFoundException}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the resource was not found.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        log.error("No resource found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("Resource not found", 404));
    }

    /**
     * Handles {@link OrderNotFoundException} and returns a 404 Not Found response.
     *
     * @param ex The caught {@link OrderNotFoundException}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the order was not found.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(OrderNotFoundException ex) {

        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage(), 404));
    }

    /**
     * Handles {@link AccessDeniedException} and returns a 403 Forbidden response.
     * This exception is typically thrown when an authenticated user attempts to access
     * a resource they do not have permission for.
     *
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} with an {@link UserExceptionHandler.ErrorResponse} indicating access denied.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedExceptions(HttpServletRequest request) {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        log.error("Action of [{}], with role [{}] to [{}:{}] denied", username, roles, method, requestUri);

        UserExceptionHandler.ErrorResponse errorResponse = new UserExceptionHandler.ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You are not allowed to execute this operation",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("X-Error-Code", ResponseCodes.ACCESS_DENIED.getMessage())
                .body(new ApiResponse<>(ResponseCodes.ACCESS_DENIED));

    }

    @ExceptionHandler(NotYourResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleNo(NotYourResourceException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(ResponseCodes.RESOURCE_MISMATCH_OR_NOT_FOUND.getHttpStatus())
                .header("X-Error-Code", ResponseCodes.ACCESS_DENIED.getMessage())
                .body(new ApiResponse<>(ResponseCodes.ACCESS_DENIED));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(UserAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(ResponseCodes.USER_ALREADY_EXISTS.getHttpStatus())
                .header("X-Error-Code", ResponseCodes.USER_ALREADY_EXISTS.getMessage())
                .body(new ApiResponse<>(ResponseCodes.USER_ALREADY_EXISTS));
    }

    @ExceptionHandler(QrFoodDataIntegrityViolation.class)
    public ResponseEntity<ApiResponse<Void>> e1(QrFoodDataIntegrityViolation ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(ResponseCodes.DATA_INTEGRITY.getHttpStatus())
                .header("X-Error-Code", ResponseCodes.DATA_INTEGRITY.getMessage())
                .body(new ApiResponse<>(ResponseCodes.DATA_INTEGRITY));
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException ex) {
        // Клиент разорвал соединение. Это не ошибка сервера.
        // Просто логируем на уровне DEBUG или WARN и ничего не отправляем в ответ.
        log.warn("Client aborted the connection: {}", ex.getMessage());
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenNotFound(TokenException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(ResponseCodes.TOKEN_NOT_FOUND.getHttpStatus())
                .header("X-Error-Code", ResponseCodes.TOKEN_NOT_FOUND.getMessage())
                .body(new ApiResponse<>(ResponseCodes.TOKEN_NOT_FOUND));
    }

    /**
     * Catches all other unhandled {@link Exception} types and returns a generic 500 Internal Server Error response.
     *
     * @param ex The caught {@link Exception}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating an internal server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Exception", ex); // Log the full exception stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                .body(ApiResponse.fail("Internal server error.", 500));
    }
}
