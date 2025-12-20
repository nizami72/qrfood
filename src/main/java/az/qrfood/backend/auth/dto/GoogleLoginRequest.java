package az.qrfood.backend.auth.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String token; // Google ID token from frontend
}
