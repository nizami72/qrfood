package az.qrfood.backend.tableassignment.service;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.repository.TableRepository;
import az.qrfood.backend.tableassignment.dto.CreateTableAssignmentDto;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.entity.TableAssignment;
import az.qrfood.backend.tableassignment.repository.TableAssignmentRepository;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link TableAssignmentService} interface.
 * <p>
 * This class provides the implementation for the operations defined in the
 * TableAssignmentService interface, such as creating, retrieving, and deleting
 * table assignments.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TableAssignmentServiceImpl implements TableAssignmentService {

    private final TableAssignmentRepository tableAssignmentRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TableAssignmentDto createTableAssignment(CreateTableAssignmentDto createDto) {
        User waiter = userRepository.findById(createDto.getWaiterId())
                .orElseThrow(() -> new EntityNotFoundException("Waiter not found with ID: " + createDto.getWaiterId()));

        // Check if the user has the WAITER role
        if (!waiter.getRoles().contains(Role.WAITER)) {
            throw new IllegalArgumentException("User with ID: " + createDto.getWaiterId() + " is not a waiter");
        }

        TableInEatery table = tableRepository.findById(createDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + createDto.getTableId()));

        // Check if the table is already assigned to a waiter
        tableAssignmentRepository.findByTable(table).stream()
                .findFirst()
                .ifPresent(assignment -> {
                    throw new IllegalStateException("Table with ID: " + createDto.getTableId() + " is already assigned to a waiter");
                });

        TableAssignment tableAssignment = TableAssignment.builder()
                .waiter(waiter)
                .table(table)
                .createdAt(LocalDateTime.now())
                .build();

        TableAssignment savedAssignment = tableAssignmentRepository.save(tableAssignment);
        return mapToDto(savedAssignment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TableAssignmentDto getTableAssignmentById(Long id) {
        TableAssignment tableAssignment = tableAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table assignment not found with ID: " + id));
        return mapToDto(tableAssignment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TableAssignmentDto> getTableAssignmentsByWaiterId(Long waiterId) {
        User waiter = userRepository.findById(waiterId)
                .orElseThrow(() -> new EntityNotFoundException("Waiter not found with ID: " + waiterId));
        return tableAssignmentRepository.findByWaiter(waiter).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TableAssignmentDto> getTableAssignmentsByTableId(Long tableId) {
        TableInEatery table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + tableId));
        return tableAssignmentRepository.findByTable(table).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTableAssignment(Long id) {
        if (!tableAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Table assignment not found with ID: " + id);
        }
        tableAssignmentRepository.deleteById(id);
    }

    /**
     * Maps a {@link TableAssignment} entity to a {@link TableAssignmentDto}.
     *
     * @param tableAssignment The entity to map.
     * @return The mapped DTO.
     */
    private TableAssignmentDto mapToDto(TableAssignment tableAssignment) {
        return TableAssignmentDto.builder()
                .id(tableAssignment.getId())
                .waiterId(tableAssignment.getWaiter().getId())
                .tableId(tableAssignment.getTable().getId())
                .createdAt(tableAssignment.getCreatedAt())
                .updatedAt(tableAssignment.getUpdatedAt())
                .build();
    }
}