package az.qrfood.backend.table.dto;

import az.qrfood.backend.qr.dto.QrCodeDto;

public record TableDto(String number,
                       Long eateryId,
                       QrCodeDto qrCodeDto) {
}
