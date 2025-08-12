package az.qrfood.backend.common.response;

import org.springframework.http.HttpStatus;

public enum ResponseCodes {


    RESOURCE_MISMATCH_OR_NOT_FOUND(
            false,
            "Access to resources that are unrelated or do not exist",
            HttpStatus.NOT_FOUND,
            "Requested resource does not exist or is not related to the given context (e.g., entity ownership mismatch)"
    ),

    INVALID_JWT(
            false,
            "Invalid or expired authentication token, please log in again",
            HttpStatus.UNAUTHORIZED,
            "JWT is missing, malformed, expired or signature verification failed"
    ),

    EATERY_MISMATCH(
            false,
            "Access denied for the requested eatery.",
            HttpStatus.CONFLICT,
            "EateryId mismatch: Access denied for the requested eatery."

    ),


    USER_ALREADY_EXISTS(
            false,
            "A user with this email already exists",
            HttpStatus.CONFLICT,
            "Attempt to register with an email that's already in use"
    ),

    INVALID_PASSWORD(
            false,
            "Password does not meet security requirements",
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Password validation failed due to length or complexity"
    ),

    ACCESS_DENIED(
            false,
            "You do not have permission to access this resource",
            HttpStatus.FORBIDDEN,
            "User attempted to access a restricted resource"
    );

    private final boolean success;
    private final String message;
    private final HttpStatus httpStatus;
    private final String debugMessage;

    ResponseCodes(boolean success, String message, HttpStatus httpStatus, String debugMessage) {
        this.success = success;
        this.message = message;
        this.httpStatus = httpStatus;
        this.debugMessage = debugMessage;
    }


    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public boolean isSuccess() {
        return success;
    }
}
