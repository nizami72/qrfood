package az.qrfood.backend.table.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.qr.dto.QrCodeDto;
import az.qrfood.backend.qr.entity.QrCode;
import az.qrfood.backend.qr.service.QrService;
import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.entity.TableStatus;
import az.qrfood.backend.table.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TableService {

    @Value("${host.name}")
    private String baseUrl;
    private final QrService qrService;
    private final TableRepository tableRepository;
    private final EateryRepository eateryRepository;

    public TableService(QrService qrService, TableRepository tableRepository, EateryRepository eateryRepository) {
        this.qrService = qrService;
        this.tableRepository = tableRepository;
        this.eateryRepository = eateryRepository;
    }

    /**
     * Find a table by eatery ID and table number
     */
    public Optional<TableDto> findByEateryAndNumber(Long eateryId, String tableNumber) {
        return tableRepository.findByEateryIdAndTableNumber(eateryId, tableNumber)
                .map(this::convertToDto);
    }

    /**
     * Find a table by ID
     */
    public Optional<TableDto> findById(Long id) {
        return tableRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * List all tables for an eatery
     */
    public List<TableDto> listTablesForEatery(Long eateryId) {
        return tableRepository.findByEateryId(eateryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new table in an eatery
     */
    @Transactional
    public TableDto createTable(TableDto tableDto) {
        Eatery eatery = eateryRepository.findById(tableDto.eateryId())
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found with id: " + tableDto.eateryId()));

        TableStatus s = tableDto.status();
        TableInEatery table = TableInEatery.builder()
                .eatery(eatery)
                .tableNumber(tableDto.number())
                .seats(tableDto.seats())
                .note(tableDto.note())
                .status(s == null ? TableStatus.ACTIVE :tableDto.status())
                .build();

        TableInEatery savedTable = tableRepository.save(table);
        table.setQrCode(qrService.createQrCodeEntity(eatery.getId(), table.getId()));
        return convertToDto(savedTable);
    }

    /**
     * Update an existing table
     */
    @Transactional
    public TableDto updateTable(Long id, TableDto tableDto) {
        TableInEatery table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));

        Eatery eatery = eateryRepository.findById(tableDto.eateryId())
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found with id: " + tableDto.eateryId()));

        table.setTableNumber(tableDto.number());
        table.setEatery(eatery);
        table.setSeats(tableDto.seats());
        table.setNote(tableDto.note());
        table.setStatus(tableDto.status());

        TableInEatery updatedTable = tableRepository.save(table);
        return convertToDto(updatedTable);
    }

    /**
     * Delete a table
     */
    @Transactional
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    /**
     * Create a table in an eatery (used by EateryService)
     */
    public TableInEatery createTableInEatery(Eatery eatery, String tableNumber) {
        TableInEatery table = TableInEatery.builder()
                .tableNumber(String.valueOf(tableNumber))
                .eatery(eatery)
                .build();

        TableInEatery savedTable = tableRepository.save(table);
        table.setQrCode(qrService.createQrCodeEntity(eatery.getId(), table.getId()));
        return tableRepository.save(savedTable);
    }

    /**
     * Convert TableInEatery entity to TableDto
     */
    private TableDto convertToDto(TableInEatery table) {
        QrCodeDto qrCodeDto = null;
        if (table.getQrCode() != null) {
            QrCode qrCode = table.getQrCode();
            qrCodeDto = new QrCodeDto(
                    qrCode.getId(),
                    qrCode.getQrCodeAsBytes(),
                    qrCode.getValidFrom(),
                    qrCode.getValidTo()
            );
        }

        return new TableDto(
                table.getId(),
                table.getTableNumber(),
                table.getSeats(),
                table.getNote(),
                table.getStatus(),
                table.getEatery().getId(),
                qrCodeDto
        );
    }
}
