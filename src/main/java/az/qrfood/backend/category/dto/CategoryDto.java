package az.qrfood.backend.category.dto;

import az.qrfood.backend.dish.dto.DishDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long categoryId;
    private Long eateryId;

    @NotBlank(message = "Name in Azerbaijani is required")
    @Size(min = 2, max = 50, message = "Name in Azerbaijani must be between 2 and 100 characters")
    private String nameAz;

    @Size(min = 0, max = 50, message = "Name in English must be between 2 and 100 characters")
    private String nameEn;

    @Size(min = 0, max = 50, message = "Name in Russian must be between 2 and 100 characters")
    private String nameRu;

    private String image;
    private List<DishDto> dishes;

}
