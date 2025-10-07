package az.qrfood.backend.dish.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class QrFoodDataIntegrityViolation extends DataIntegrityViolationException {

    public QrFoodDataIntegrityViolation(String message) {
        super(message);
    }
}