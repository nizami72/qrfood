package az.qrfood.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) for the registration request.
 * Contains user information and restaurant information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private UserDto user;
    private RestaurantDto restaurant;

    /**
     * DTO for user information in the registration request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private String name;
        private String email;
        private String password;
    }

    /**
     * DTO for restaurant information in the registration request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantDto {
        private String name;
    }
}