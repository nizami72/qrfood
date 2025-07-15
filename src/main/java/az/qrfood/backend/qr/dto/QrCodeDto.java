package az.qrfood.backend.qr.dto;

import java.time.LocalDateTime;

public record QrCodeDto(Long id,
                        byte[] qrCodeAsBytes,
                        LocalDateTime validFrom,
                        LocalDateTime validTo,
                        String content) {
}
