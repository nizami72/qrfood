package az.qrfood.backend.order.dto;

public record ClientDeviceDto(Long eateryId,
                              String eateryName,
                              Long tableId,
                              String tableName,
                              boolean hasOrders
) {
}
