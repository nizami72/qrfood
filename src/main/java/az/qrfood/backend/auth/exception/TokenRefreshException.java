package az.qrfood.backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is an issue with refreshing a token.
 * <p>
 * This exception is thrown when a refresh token is invalid, expired, or otherwise cannot be used
 * to generate a new access token.
 * </p>
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

    /**
     * Constructs a new token refresh exception with the specified detail message.
     *
     * @param token The refresh token that caused the exception.
     * @param message The detail message.
     */
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}