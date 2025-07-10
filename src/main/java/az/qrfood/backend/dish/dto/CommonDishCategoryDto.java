package az.qrfood.backend.dish.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Represents a category of predefined common dishes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonDishCategoryDto {
    private String categoryId; // This should match the actual category ID in the system
    private String nameAz;
    private String nameEn;
    private String nameRu;
    private List<CommonDishDto> dishes;
}