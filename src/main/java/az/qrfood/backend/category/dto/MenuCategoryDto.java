package az.qrfood.backend.category.dto;

import az.qrfood.backend.menu.dto.MenuItemDto;
import az.qrfood.backend.menu.entity.MenuItem;
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

    private String nameAz;
    private String nameEn;
    private String nameRu;
    private Long eateryId;
    private Long categoryId;
    private List<MenuItemDto> items;

}