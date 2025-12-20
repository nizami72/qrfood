package az.qrfood.backend.auth.dto;

import lombok.Data;

@Data
public class VerifyTokenRequest {
    private String token;
}
