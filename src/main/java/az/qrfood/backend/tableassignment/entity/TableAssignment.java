package az.qrfood.backend.tableassignment.entity;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents an assignment of a table to a waiter in the restaurant.
 * <p>
 * This entity stores the relationship between a waiter (User with WAITER role)
 * and a table in the restaurant (TableInEatery). It also tracks when the assignment
 * was created and when it was last updated.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "table_assignment")
@Builder
public class TableAssignment {

    /**
     * The unique identifier for the table assignment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The waiter to whom the table is assigned.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    /**
     * The table that is assigned to the waiter.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableInEatery table;

    /**
     * The timestamp when the assignment was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the assignment was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Provides a string representation of the TableAssignment object.
     *
     * @return A string representation of the table assignment.
     */
    @Override
    public String toString() {
        return "TableAssignment{" +
                "id=" + id +
                ", waiter=" + (waiter != null ? waiter.getId() : null) +
                ", table=" + (table != null ? table.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}