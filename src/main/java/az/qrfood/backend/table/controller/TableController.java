package az.qrfood.backend.table.controller;

import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.service.TableService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@Log4j2
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * GET all tables for a specific eatery
     */
    @GetMapping("/eatery/{eateryId}")
    public ResponseEntity<List<TableDto>> getTables(@PathVariable Long eateryId) {
        return ResponseEntity.ok(tableService.listTablesForEatery(eateryId));
    }

    /**
     * GET a specific table by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<TableDto> getTable(@PathVariable Long id) {
        return tableService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST a new table
     */
    @PostMapping
    public ResponseEntity<TableDto> createTable(@RequestBody TableDto tableDto) {
        try {
            TableDto createdTable = tableService.createTable(tableDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT updated information for a specific table
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableDto> updateTable(@PathVariable Long id, @RequestBody TableDto tableDto) {
        try {
            TableDto updatedTable = tableService.updateTable(id, tableDto);
            return ResponseEntity.ok(updatedTable);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE a specific table by id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
