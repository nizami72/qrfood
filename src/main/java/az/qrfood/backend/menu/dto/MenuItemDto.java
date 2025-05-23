package az.qrfood.backend.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Represents a menu item (dish or drink) available in a restaurant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {

    private Long dishId;
    private Long categoryId;
    private String nameAz;
    private String nameEn;
    private String nameRu;
    private String descriptionAz;
    private String descriptionEn;
    private String descriptionRu;
    private BigDecimal price;
    private String imageUrl;
    private boolean isAvailable = true;


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
                descriptionEn, descriptionRu, price, imageUrl, isAvailable
        );
    }

}