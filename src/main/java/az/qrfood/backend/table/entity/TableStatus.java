package az.qrfood.backend.table.entity;

/**
 * Enumeration representing the possible statuses of a table in an eatery.
 * <p>
 * This enum defines the various states a table can be in, which helps in
 * managing table availability and operations within the restaurant.
 * </p>
 */
public enum TableStatus {
    /**
     * The table is not currently in use or available for customers.
     */
    INACTIVE,
    /**
     * The table is active and available for customers.
     */
    ACTIVE,
    /**
     * The table is currently occupied by customers.
     */
    BUSY,
    /**
     * The table has been reserved for future use.
     */
    RESERVED,

    ARCHIVED,
}
