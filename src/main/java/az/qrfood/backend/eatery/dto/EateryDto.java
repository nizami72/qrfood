package az.qrfood.backend.eatery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class EateryDto {

    private Long eateryId;
    private String name;
    private String address;
    private List<String> phones;
    private int tables;
    private List<String> categories;
    private Double geoLat;
    private Double geoLng;
}