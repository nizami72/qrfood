package az.qrfood.backend.table.controller;

import az.qrfood.backend.table.entity.QrCode;
import az.qrfood.backend.table.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<QrCode>> getTables(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(tableService.listTablesForRestaurant(restaurantId));
    }
}
