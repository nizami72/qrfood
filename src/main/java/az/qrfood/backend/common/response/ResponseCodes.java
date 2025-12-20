package az.qrfood.backend.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ResponseCodes {


    RESOURCE_MISMATCH_OR_NOT_FOUND(
            false,
            "Access to resources that are unrelated or do not exist",
            "response.notYourResource",
            HttpStatus.NOT_FOUND,
            "Requested resource does not exist or is not related to the given context (e.g., entity ownership mismatch)"
    ),

    INVALID_JWT(
            false,
            "Invalid or expired authentication token, please log in again",
            "response.jwtInvalid",
            HttpStatus.UNAUTHORIZED,
            "JWT is missing, malformed, expired or signature verification failed"
    ),

    EATERY_MISMATCH(
            false,
            "Access denied for the requested eatery.",
            "response.notYourEatery",
            HttpStatus.CONFLICT,
            "EateryId mismatch: Access denied for the requested eatery."

    ),

    EATERY_ID_MISSING(
            false,
            "Access denied, eatery id must be set in token.",
            "response.notEateryIdInJwt",
            HttpStatus.BAD_REQUEST,
            "Access denied duw to missing eatery ID."

    ),

    USER_ALREADY_EXISTS(
            false,
            "A user with this email already exists",
            "response.userExist",
            HttpStatus.CONFLICT,
            "Attempt to register with an email that's already in use"
    ),

    INVALID_PASSWORD(
            false,
            "Password does not meet security requirements",
            "response.invalidPass",
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Password validation failed due to length or complexity"
    ),

    ACCESS_DENIED(
            false,
            "You do not have permission to access this resource",
            "response.accessDenied", HttpStatus.FORBIDDEN,
            "User attempted to access a restricted resource"
    ),

    DATA_INTEGRITY(
            false,
            "Cannot delete dish because it is referenced by order items",
            "response.integrityViolation", HttpStatus.FORBIDDEN,
            "User attempted to delete dish referenced by order items"
    ),

    TOKEN_NOT_FOUND(
            false,
                    "Token not found",
                    "response.notValidatedUser",
            HttpStatus.FORBIDDEN,
            "User attempted to access an unvalidated account"
    )



    ;

    @Getter
    private final boolean success;
    @Getter
    private final String message;
    @Getter
    private final String messageKey;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String debugMessage;

    ResponseCodes(boolean success, String message, String messageKey, HttpStatus httpStatus, String debugMessage) {
        this.success = success;
        this.message = message;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.debugMessage = debugMessage;
    }
}
