package az.qrfood.backend.tableassignment.repository;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.tableassignment.entity.TableAssignment;
import az.qrfood.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link TableAssignment} entities.
 * <p>
 * This interface provides CRUD operations for the TableAssignment entity,
 * as well as custom query methods for finding table assignments by waiter and table.
 * </p>
 */
@Repository
public interface TableAssignmentRepository extends JpaRepository<TableAssignment, Long> {

    /**
     * Finds all table assignments for a specific waiter.
     *
     * @param waiter The waiter for whom to find table assignments.
     * @return A list of table assignments for the specified waiter.
     */
    List<TableAssignment> findByWaiter(User waiter);

    /**
     * Finds all table assignments for a specific table.
     *
     * @param table The table for which to find assignments.
     * @return A list of table assignments for the specified table.
     */
    List<TableAssignment> findByTable(TableInEatery table);

    /**
     * Finds a table assignment for a specific waiter and table.
     *
     * @param waiter The waiter for whom to find the table assignment.
     * @param table The table for which to find the assignment.
     * @return An Optional containing the table assignment if found, or empty if not found.
     */
    Optional<TableAssignment> findByWaiterAndTable(User waiter, TableInEatery table);
}