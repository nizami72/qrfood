package az.qrfood.backend.table.service;

import az.qrfood.backend.common.QrCodeGenerator;
import az.qrfood.backend.common.Util;
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

/**
 * Service class for managing {@link TableInEatery} entities.
 * <p>
 * This service handles the business logic related to tables within eateries,
 * including CRUD operations, QR code generation for tables, and conversion
 * between table entities and DTOs.
 * </p>
 */
@Service
public class TableService {

    @Value("${host.name}")
    private String baseUrl;
    private final QrService qrService;
    private final TableRepository tableRepository;
    private final EateryRepository eateryRepository;

    /**
     * Constructs a TableService with necessary dependencies.
     *
     * @param qrService        The service for QR code operations.
     * @param tableRepository  The repository for TableInEatery entities.
     * @param eateryRepository The repository for Eatery entities.
     */
    public TableService(QrService qrService, TableRepository tableRepository, EateryRepository eateryRepository) {
        this.qrService = qrService;
        this.tableRepository = tableRepository;
        this.eateryRepository = eateryRepository;
    }

    /**
     * Finds a table by its eatery ID and table number.
     *
     * @param eateryId    The ID of the eatery.
     * @param tableNumber The number of the table.
     * @return An {@link Optional} containing the found {@link TableDto}, or empty if not found.
     */
    public Optional<TableDto> findByEateryAndNumber(Long eateryId, String tableNumber) {
        return tableRepository.findByEateryIdAndTableNumber(eateryId, tableNumber)
                .map(this::convertToDto);
    }

    /**
     * Finds a table by its unique ID.
     *
     * @param id The ID of the table to find.
     * @return An {@link Optional} containing the found {@link TableDto}, or empty if not found.
     */
    public Optional<TableDto> findById(Long id) {
        return tableRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * List all tables for an eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link TableDto} representing all tables in the specified eatery.
     */
    public List<TableDto> listTablesForEatery(Long eateryId) {
        return tableRepository.findByEateryId(eateryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new table in an eatery.
     * <p>
     * This method also generates a QR code for the new table and associates it.
     * </p>
     *
     * @param tableDto The {@link TableDto} containing the details for the new table.
     * @return The newly created {@link TableDto}.
     * @throws EntityNotFoundException if the specified eatery is not found.
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
                .status(s == null ? TableStatus.ACTIVE : tableDto.status())
                .build();

        TableInEatery savedTable = tableRepository.save(table);
        table.setQrCode(qrService.createQrCodeEntity(eatery.getId(), table.getId()));
        return convertToDto(savedTable);
    }

    /**
     * Updates an existing table.
     *
     * @param id       The ID of the table to update.
     * @param tableDto The {@link TableDto} containing the updated table data.
     * @return The updated {@link TableDto}.
     * @throws EntityNotFoundException if the table or eatery is not found.
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
     * Deletes a table by its ID.
     *
     * @param id The ID of the table to delete.
     */
    @Transactional
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    /**
     * Creates a table within an eatery. This method is specifically used by {@link az.qrfood.backend.eatery.service.EateryService}.
     * <p>
     * It generates a QR code for the new table and associates it.
     * </p>
     *
     * @param eatery      The {@link Eatery} to which the table belongs.
     * @param tableNumber The number or identifier for the new table.
     * @return The newly created {@link TableInEatery} entity.
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
     * Converts a {@link TableInEatery} entity to a {@link TableDto}.
     * <p>
     * This method maps the entity's properties to the DTO, including its associated QR code.
     * </p>
     *
     * @param table The {@link TableInEatery} entity to convert.
     * @return The converted {@link TableDto}.
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
