package az.qrfood.backend.user.dto;

import az.qrfood.backend.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * DTO (Data Transfer Object) for the registration request.
 * Contains user information and restaurant information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private UserDto user;
    private RestaurantDto restaurant;
    private UserProfileRequest userProfileRequest;

    /**
     * DTO for user information in the registration request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDto {
        private String email;
        private String password;
        private Set<Role> roles;
    }

    /**
     * DTO for restaurant information in the registration request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestaurantDto {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfileRequest{
        private String name;
        private String phone;
    }

}

