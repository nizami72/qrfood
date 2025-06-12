package az.qrfood.backend.common.exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String message) {
        super(message);
    }
}