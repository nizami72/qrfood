package az.qrfood.backend.order.entity;

import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.table.entity.TableInEatery;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a customer order placed at a specific table in an eatery.
 * <p>
 * This entity captures details about an order, including the table it originated from,
 * its creation timestamp, current status, any special notes, and a list of ordered items.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`order`") // Enclosed in backticks because "order" is a SQL keyword
public class Order {

    /**
     * The unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The table from which this order was placed.
     * This is a many-to-one relationship, linking an order to a specific table.
     */
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableInEatery table;

    /**
     * The timestamp when the order was created.
     * Defaults to the current time when the entity is persisted.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * The current status of the order.
     * Stored as a string in the database, mapped from the {@link OrderStatus} enum.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    /**
     * Any additional notes or special requests for the order.
     */
    private String note;

    /**
     * A list of individual items included in this order.
     * This is a one-to-many relationship, where one order can contain multiple items.
     * The {@code CascadeType.ALL} ensures that all operations (persist, merge, remove, refresh, detach)
     * are cascaded to the associated order items.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
