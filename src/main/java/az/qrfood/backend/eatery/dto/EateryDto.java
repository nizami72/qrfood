package az.qrfood.backend.eatery.dto;

import az.qrfood.backend.category.dto.DishCategoryDto;
import az.qrfood.backend.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class EateryDto {

    public EateryDto() {
        phones = new ArrayList<>();
        tableIds = new ArrayList<>();
        categoryIds = new ArrayList<>();
    }

    private Long eateryId;
    private String name;
    private String address;
    private List<String> phones;
    private List<Long> tableIds;
    private List<Long> categoryIds;
    private List<DishCategoryDto> categories;
    private int tablesAmount;
    private Double geoLat;
    private Double geoLng;
}