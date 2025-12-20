package az.qrfood.backend.auth.dto;

import lombok.Data;

@Data
public class MagicLinkRequest {
    private String email;
}
