package az.qrfood.backend.table.service;

import az.qrfood.backend.table.entity.QrCode;
import az.qrfood.backend.table.repository.TableQRRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    private final TableQRRepository tableQRRepository;

    public TableService(TableQRRepository tableQRRepository) {
        this.tableQRRepository = tableQRRepository;
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
}
