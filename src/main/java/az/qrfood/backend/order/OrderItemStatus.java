package az.qrfood.backend.order;

/**
 * Enumeration representing the possible statuses of an order item.
 * <p>
 * This enum defines the lifecycle of an order item within the QR Food Order system,
 * from creation to serving or deletion.
 * </p>
 */
public enum OrderItemStatus {
    /**
     * The order item has just been created.
     */
    CREATED,

    /**
     * The order item is currently being prepared (e.g., in the kitchen).
     */
    PREPARING,

    /**
     * The order item is ready for serving.
     */
    READY,

    /**
     * The order item has been served to the customer.
     */
    SERVED,

    /**
     * The order item has been deleted.
     */
    DELETED;
}