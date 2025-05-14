package az.qrfood.backend.restaurant.dto;

import az.qrfood.backend.menu.entity.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String address;
    private List<String> phones;
    private int tablesAmount;
    private List<MenuCategory> categories;
    private Double geoLat;
    private Double geoLng;
}