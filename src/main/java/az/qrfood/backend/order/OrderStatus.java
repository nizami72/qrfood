package az.qrfood.backend.order;

/**
 * Enumeration representing the possible statuses of an order.
 * <p>
 * This enum defines the lifecycle of an order within the QR Food Order system,
 * from creation to completion or cancellation.
 * </p>
 */
public enum OrderStatus {
    /**
     * The order has just been created.
     */
    CREATED,

    /**
     * The order is currently being prepared (e.g., in the kitchen).
     */
    PREPARING,

    /**
     * The order is ready for pickup or delivery.
     */
    READY,

    /**
     * The order has been paid for.
     */
    PAID,

    /**
     * The order creation process has started but is not yet complete.
     */
    IN_PROGRESS,

    /**
     * The order has been cancelled.
     */
    CANCELLED;

    /**
     * Alias for {@link #CREATED} for backward compatibility.
     */
    public static final OrderStatus NEW = CREATED;
}
