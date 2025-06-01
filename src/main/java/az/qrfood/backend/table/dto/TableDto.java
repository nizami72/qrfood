package az.qrfood.backend.table.dto;

import az.qrfood.backend.qr.dto.QrCodeDto;
import az.qrfood.backend.table.entity.TableStatus;

public record TableDto(
        Long id,
        String number,
        int seats,
        String note,
        TableStatus status,
        Long eateryId,
        QrCodeDto qrCodeDto) {
}
