package az.qrfood.backend.dish.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Represents a dish item (dish or drink) available in a restaurant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishDto {

    private Long dishId;

    private Long categoryId;

    @NotBlank(message = "Name in Azerbaijani is required")
    @Size(min = 2, max = 50, message = "Name in Azerbaijani must not exceed characters")
    private String nameAz;

    @Size(min = 0, max = 50, message = "Name in English must not exceed 50 characters")
    private String nameEn;

    @Size(min = 0, max = 50, message = "Name in Russian must not exceed 50 characters")
    private String nameRu;

    @Size(max = 500, message = "Description in Azerbaijani must not exceed 500 characters")
    private String descriptionAz;

    @Size(max = 500, message = "Description in English must not exceed 500 characters")
    private String descriptionEn;

    @Size(max = 500, message = "Description in Russian must not exceed 500 characters")
    private String descriptionRu;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private BigDecimal price;

    private String image;

    private boolean isAvailable = true;


    public boolean isIsAvailable(){
        return isAvailable;
    }

    @Override
    public String toString() {
        return String.format("""
        Dish {
            categoryId: %s
            nameAz        : %s
            nameEn        : %s
            nameRu        : %s
            descriptionAz : %s
            descriptionEn : %s
            descriptionRu : %s
            price         : %s
            imageUrl      : %s
            isAvailable   : %s
        }
        """,
                categoryId, nameAz, nameEn, nameRu, descriptionAz,
                descriptionEn, descriptionRu, price, image, isAvailable
        );
    }

}
