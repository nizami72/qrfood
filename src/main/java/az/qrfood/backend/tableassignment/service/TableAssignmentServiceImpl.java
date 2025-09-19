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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        // Check if the waiter is already assigned to this table
        tableAssignmentRepository.findByWaiterAndTable(waiter, table)
                .ifPresent(assignment -> {
                    throw new IllegalStateException("Waiter with ID: " + createDto.getWaiterId() + 
                            " is already assigned to table with ID: " + createDto.getTableId());
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
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID must not be null");
        }

        TableAssignment tableAssignment = tableAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table assignment not found with ID: " + id));
        return mapToDto(tableAssignment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TableAssignmentDto> getAllTableAssignments(Long eateryId) {
        if (eateryId == null) {
            throw new IllegalArgumentException("Eatery ID must not be null");
        }

        // Get all table assignments and filter by eatery ID
        return tableAssignmentRepository.findAll().stream()
                .filter(assignment -> assignment.getTable() != null 
                        && assignment.getTable().getEatery() != null 
                        && eateryId.equals(assignment.getTable().getEatery().getId()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TableAssignmentDto> getTableAssignmentsByWaiterId(Long waiterId) {
        if (waiterId == null) {
            throw new IllegalArgumentException("Waiter ID must not be null");
        }

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
        if (tableId == null) {
            throw new IllegalArgumentException("Table ID must not be null");
        }

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
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID must not be null");
        }

        if (!tableAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Table assignment not found with ID: " + id);
        }
        log.debug("Deleting table assigment with id [{}]", id);
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
