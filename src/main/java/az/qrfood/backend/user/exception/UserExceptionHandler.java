package az.qrfood.backend.user.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for user-related exceptions within the application.
 * <p>
 * This class uses Spring's {@code @ControllerAdvice} to provide centralized
 * exception handling across all controllers, returning consistent error responses.
 * </p>
 */
@ControllerAdvice
@Log4j2
public class UserExceptionHandler {

    /**
     * Handles {@link EntityNotFoundException} and returns a 404 Not Found response.
     * This exception is typically thrown when a requested user entity is not found in the database.
     *
     * @param ex The caught {@link EntityNotFoundException}.
     * @return A {@link ResponseEntity} with an {@link ErrorResponse} indicating the error.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link UserAlreadyExistsException} and returns a 409 Conflict response.
     * This exception is thrown when attempting to create a user that already exists.
     *
     * @param ex The caught {@link UserAlreadyExistsException}.
     * @return A {@link ResponseEntity} with an {@link ErrorResponse} indicating the conflict.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    /**
     * Handles {@link IllegalStateException} and returns a 409 Conflict response.
     * This exception is typically thrown when an operation is performed in an inappropriate
     * state (e.g., attempting to create a user that already exists).
     *
     * @param ex The caught {@link IllegalStateException}.
     * @return A {@link ResponseEntity} with an {@link ErrorResponse} indicating the conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} (validation errors) and returns a 400 Bad Request response.
     * It extracts field-specific error messages and includes them in the response.
     *
     * @param ex The caught {@link MethodArgumentNotValidException}.
     * @return A {@link ResponseEntity} with a {@link ValidationErrorResponse} containing validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unhandled {@link Exception} types and returns a generic 500 Internal Server Error response.
     *
     * @param ex The caught {@link Exception}.
     * @return A {@link ResponseEntity} with an {@link ErrorResponse} indicating an internal server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * A static nested class representing a generic error response structure.
     */
    public static class ErrorResponse {
        private final int status;
        private final String message;
        private final LocalDateTime timestamp;

        /**
         * Constructs a new ErrorResponse.
         *
         * @param status    The HTTP status code.
         * @param message   A descriptive error message.
         * @param timestamp The timestamp when the error occurred.
         */
        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * A static nested class representing a validation error response, extending {@link ErrorResponse}.
     * It includes a map of field-specific errors.
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> errors;

        /**
         * Constructs a new ValidationErrorResponse.
         *
         * @param status    The HTTP status code.
         * @param message   A descriptive error message.
         * @param timestamp The timestamp when the error occurred.
         * @param errors    A map where keys are field names and values are error messages for those fields.
         */
        public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
            super(status, message, timestamp);
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
