package az.qrfood.backend.common.exception;

import az.qrfood.backend.common.response.ApiResponse;
import az.qrfood.backend.common.response.ResponseCodes;
import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.user.exception.UserAlreadyExistsException;
import az.qrfood.backend.user.exception.UserExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
     * @param ex      The caught {@link OrderNotFoundException}.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the order was not found.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(OrderNotFoundException ex,
                                                                 HttpServletRequest request) {

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
     * @param ex      The caught {@link AccessDeniedException}.
     * @return A {@link ResponseEntity} with an {@link UserExceptionHandler.ErrorResponse} indicating access denied.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<UserExceptionHandler.ErrorResponse> handleAccessDeniedExceptions(HttpServletRequest request,
                                                                                           AccessDeniedException ex) {

        String requestUri = request.getRequestURI();
        log.error("Access to [{}] denied", requestUri);

        UserExceptionHandler.ErrorResponse errorResponse = new UserExceptionHandler.ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You are not allowed to execute this operation",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(NotYourResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleNo(NotYourResourceException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(ResponseCodes.RESOURCE_MISMATCH_OR_NOT_FOUND.getHttpStatus())
                .body(new ApiResponse<Void>(ResponseCodes.RESOURCE_MISMATCH_OR_NOT_FOUND));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(UserAlreadyExistsException ex) {
        log.error(ex.getMessage());
         return ResponseEntity
            .status(ResponseCodes.USER_ALREADY_EXISTS.getHttpStatus())
            .header("X-Error-Code", ResponseCodes.USER_ALREADY_EXISTS.getMessage())
            .body(new ApiResponse<Void>(ResponseCodes.USER_ALREADY_EXISTS));
    }

    /**
     * Catches all other unhandled {@link Exception} types and returns a generic 500 Internal Server Error response.
     *
     * @param ex The caught {@link Exception}.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating an internal server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("An unexpected error occurred", ex); // Log the full exception stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Internal server error.", 500));
    }
}
