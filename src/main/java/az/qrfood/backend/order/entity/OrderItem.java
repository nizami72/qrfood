package az.qrfood.backend.order.entity;

import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.order.OrderItemStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Represents a single item within an {@link Order}.
 * <p>
 * This entity captures details about a specific dish that has been ordered,
 * including its quantity, any special notes, and the price at the time of order.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    /**
     * The unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The {@link Order} to which this item belongs.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The {@link DishEntity} that this order item represents.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private DishEntity dishEntity;

    /**
     * The quantity of the dish ordered.
     */
    private int quantity;

    /**
     * Any special notes or customizations for this specific order item.
     */
    private String note;

    /**
     * The price of the dish at the time the order was placed.
     * This ensures that the price is fixed even if the dish's price changes later.
     */
    private BigDecimal priceAtOrder;

    /**
     * The current status of this order item.
     * Default status is CREATED when a new order item is created.
     */
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status = OrderItemStatus.CREATED;
}
