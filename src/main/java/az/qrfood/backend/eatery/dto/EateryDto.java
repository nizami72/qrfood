package az.qrfood.backend.eatery.dto;

import az.qrfood.backend.category.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    private Long id;
    private String name;
    private String address;
    private List<String> phones;
    private List<Long> tableIds;
    private List<Long> categoryIds;
    private List<CategoryDto> categories;
    private int tablesAmount;
    private Double geoLat;
    private Double geoLng;
    private Long ownerProfileId;
}
