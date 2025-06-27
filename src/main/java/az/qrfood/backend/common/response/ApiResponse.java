package az.qrfood.backend.common.response;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/**
 * A generic API response class for consistent data transfer between the backend and frontend.
 * <p>
 * This class encapsulates the success status of an operation, a descriptive message,
 * the actual data payload (if any), an HTTP status code, and a timestamp.
 * It provides static factory methods for creating success and failure responses.
 * </p>
 *
 * @param <T> The type of the data payload.
 */
@Getter
@Setter
public class ApiResponse<T> {

    /**
     * Indicates whether the API operation was successful.
     * {@code true} for success, {@code false} for failure.
     */
    private boolean success;

    /**
     * A human-readable message describing the outcome of the API operation.
     */
    private String message;

    /**
     * The actual data payload returned by the API operation.
     * This can be of any type and is {@code null} for error responses or operations without a data payload.
     */
    private T data;

    /**
     * The HTTP status code associated with the API response (e.g., 200 for OK, 400 for Bad Request, 500 for Internal Server Error).
     */
    private int code;

    /**
     * The timestamp when the API response was generated, in UTC.
     */
    private Instant timestamp;

    /**
     * Constructs a new ApiResponse.
     *
     * @param success   Indicates whether the operation was successful.
     * @param message   A descriptive message about the operation's outcome.
     * @param data      The data payload of the response.
     * @param code      The HTTP status code.
     */
    public ApiResponse(boolean success, String message, T data, int code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
        this.timestamp = Instant.now();
    }

    /**
     * Creates a successful API response with a message and data.
     * The status code is automatically set to 200 (OK).
     *
     * @param message A success message.
     * @param data    The data payload.
     * @param <T>     The type of the data payload.
     * @return A new {@link ApiResponse} instance representing a successful operation.
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    /**
     * Creates a failed API response with an error message and a custom status code.
     * The data payload is set to {@code null}.
     *
     * @param message An error message.
     * @param code    The HTTP status code for the error.
     * @param <T>     The type of the data payload (will be {@code Void} or {@code Object} for error responses).
     * @return A new {@link ApiResponse} instance representing a failed operation.
     */
    public static <T> ApiResponse<T> fail(String message, int code) {
        return new ApiResponse<>(false, message, null, code);
    }
}
