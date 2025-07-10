package az.qrfood.backend.dish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Represents a predefined common dish template available for quick addition.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonDishDto {
    private String nameAz;
    private String nameEn;
    private String nameRu;
    private String descriptionAz;
    private String descriptionEn;
    private String descriptionRu;
    private BigDecimal price;
    private String image;
    private boolean selected; // Used in UI to track which dishes are selected
}