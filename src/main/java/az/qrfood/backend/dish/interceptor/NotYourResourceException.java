package az.qrfood.backend.dish.interceptor;

public class NotYourResourceException extends RuntimeException {

    private String message;

    public NotYourResourceException(String message) {
        super(message);
        this.message = message;
    }

}