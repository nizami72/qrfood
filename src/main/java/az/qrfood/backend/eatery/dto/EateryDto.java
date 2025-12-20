package az.qrfood.backend.eatery.dto;

import az.qrfood.backend.category.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for {@link az.qrfood.backend.eatery.entity.Eatery} entities.
 * <p>
 * This DTO is used to transfer eatery data between the client and the server,
 * providing a simplified view of the Eatery entity, including its associated
 * phone numbers, table IDs, and category IDs.
 * </p>
 */
@Data
@AllArgsConstructor
@Builder
public class EateryDto {

    /**
     * The unique identifier of the eatery.
     */
    private Long id;

    /**
     * The name of the eatery.
     */
    private String name;

    /**
     * The address of the eatery.
     */
    private String address;

    /**
     * A list of phone numbers associated with the eatery.
     */
    private List<String> phones;

    /**
     * A list of IDs of tables belonging to this eatery.
     */
    private List<Long> tableIds;

    /**
     * A list of IDs of categories offered by this eatery.
     */
    private List<Long> categoryIds;

    /**
     * A list of Category DTOs associated with this eatery.
     * This might be populated when a more detailed view of categories is needed.
     */
    private List<CategoryDto> categories;

    /**
     * The total number of tables associated with this eatery.
     */
    private int numberOfTables;

    /**
     * The geographical latitude of the eatery.
     */
    private Double geoLat;

    /**
     * The geographical longitude of the eatery.
     */
    private Double geoLng;

    /**
     * The ID of the user profile that owns this eatery.
     */
    private Long ownerProfileId;

    private String ownerMail;

    private OnboardingStatus onboardingStatus;

    /**
     * Default constructor. Initializes lists to prevent NullPointerExceptions.
     */
    public EateryDto() {
        phones = new ArrayList<>();
        tableIds = new ArrayList<>();
        categoryIds = new ArrayList<>();
    }
}
