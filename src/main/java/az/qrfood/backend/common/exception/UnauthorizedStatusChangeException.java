package az.qrfood.backend.common.exception;

public class UnauthorizedStatusChangeException extends RuntimeException {

    public UnauthorizedStatusChangeException() {
        super("User is not authorized to change the order status.");
    }

    public UnauthorizedStatusChangeException(String message) {
        super(message);
    }

    public UnauthorizedStatusChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedStatusChangeException(Throwable cause) {
        super(cause);
    }
}
