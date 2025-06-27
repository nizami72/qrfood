package az.qrfood.backend.user.entity;

/**
 * Enumeration representing the different roles a user can have in the system.
 * <p>
 * Each role defines a set of permissions and responsibilities within the QR Food Order application.
 * </p>
 */
public enum Role {
    /**
     * System administrator with full control over all aspects of the application.
     */
    SUPER_ADMIN,
    /**
     * Administrator of a specific restaurant, managing menus, users, and other restaurant-specific settings.
     */
    EATERY_ADMIN,
    /**
     * Kitchen administrator, primarily responsible for viewing and updating order statuses.
     */
    KITCHEN_ADMIN,
    /**
     * Waiter role, with permissions to change the status of orders.
     */
    WAITER,
    /**
     * Cashier role, responsible for viewing receipts and processing payments.
     */
    CASHIER;

    /**
     * Converts a string value to its corresponding {@link Role} enum.
     * <p>
     * The conversion is case-insensitive. If the provided string does not match
     * any defined role, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param value The string representation of the role (e.g., "super_admin", "EATERY_ADMIN").
     * @return The corresponding {@link Role} enum.
     * @throws IllegalArgumentException if the provided string does not match any known role.
     */
    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

}
