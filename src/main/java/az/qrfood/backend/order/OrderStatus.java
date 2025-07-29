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
     * The order has been cancelled.
     */
    CANCELLED;

}
