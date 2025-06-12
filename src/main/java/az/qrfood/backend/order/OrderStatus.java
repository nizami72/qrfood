package az.qrfood.backend.order;

/**
 * Enum for order status, aligned with frontend expectations.
 */
public enum OrderStatus {
    /**
     * Order has just been created
     */
    CREATED,

    /**
     * Order is being processed
     */
//    PROCESSING,

    /**
     * Order is being prepared
     */
    PREPARING,

    /**
     * Order is ready for delivery
     */
    READY,

    /**
     * Order has been issued to the customer
     */
//    ISSUED,

    /**
     * Order has been paid
     */
    PAID,
    /**
     * Order creation started
     */
    IN_PROGRESS,

    /**
     * Order has been cancelled
     */
    CANCELLED;

    // For backward compatibility
    public static final OrderStatus NEW = CREATED;
//    public static final OrderStatus DELIVERED = ISSUED;
}
