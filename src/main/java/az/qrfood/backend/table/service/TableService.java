package az.qrfood.backend.table.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.qr.entity.QrCode;
import az.qrfood.backend.qr.service.QrService;
import az.qrfood.backend.table.entity.TableInEatery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    @Value("${host.name}")
    private String baseUrl;
    private final QrService qrService;

    public TableService(QrService qrService) {
        this.qrService = qrService;
    }

    /**
     * Получить столик по restaurantId и номеру стола (например, при сканировании QR).
     */
    public Optional<QrCode> findByRestaurantAndNumber(Long restaurantId, Integer tableNumber) {
        return null;
//        return tableQRRepository.findByRestaurantIdAndTableNumber(restaurantId, tableNumber);
    }

    public List<QrCode> listTablesForRestaurant(Long restaurantId) {
        return null;
//        return tableQRRepository.findByRestaurantId(restaurantId);
    }

    public TableInEatery createTableInEatery(Eatery eatery, int tableNumber) {
        return TableInEatery.builder()
                .tableNumber(String.valueOf(tableNumber))
                .restaurant(eatery)
                .qrCode(qrService.createQrCodeEntity(eatery.getId(), tableNumber, baseUrl))
                .build();
    }






}
