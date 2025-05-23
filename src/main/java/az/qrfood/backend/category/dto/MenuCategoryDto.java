package az.qrfood.backend.category.dto;

import az.qrfood.backend.menu.dto.MenuItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryDto {

    private Long categoryId;
    private Long eateryId;
    private String nameAz;
    private String nameEn;
    private String nameRu;
    private List<MenuItemDto> dishes;

}