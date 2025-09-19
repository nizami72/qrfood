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
     * The order is in progress (at least one item is being prepared).
     */
    PREPARING,

    /**
     * The order is ready for pickup (all items are ready).
     */
    READY,

    /**
     * The order has been served (all items have been served).
     */
    SERVED,

    /**
     *
     * The order has been paid for.
     */
    PAID,

    /**
     * The order has been cancelled.
     */
    CANCELLED;

}
