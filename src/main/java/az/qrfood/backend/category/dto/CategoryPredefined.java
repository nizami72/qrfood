package az.qrfood.backend.category.dto;

import az.qrfood.backend.dish.dto.DishDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryPredefined {

    private String nameAz;
    private String nameEn;
    private String nameRu;
    private String image;
    private boolean enabled;

}