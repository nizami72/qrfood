package az.qrfood.backend.user.dto;

public record RegisterResponse(Long userId, Long eateryId, String name, String message, boolean success) {

}