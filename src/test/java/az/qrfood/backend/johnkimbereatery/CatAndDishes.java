package az.qrfood.backend.johnkimbereatery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the JSON structure in the CatAndDishes.json file.
 * This class is used for testing purposes in the JohnKimberEateryTest package.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatAndDishes {
    private User user;
    private List<Eatery> eateries;

    /**
     * Represents a user in the system.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String name;
        private String email;
        private String password;
        private List<String> roles;
        private String phone;
    }

    /**
     * Represents an eatery (restaurant) in the system.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Eatery {
        private String name;
        private String address;
        private Double geoLat;
        private Double geoLng;
        private Integer numberOfTables;
        private List<String> phones;
        private List<Category> categories;
    }

    /**
     * Represents a category of dishes in an eatery.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        private String nameAz;
        private String nameEn;
        private String nameRu;
        private String image;
        private List<Dish> dishes;
    }

    /**
     * Represents a dish in a category.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dish {
        private String nameAz;
        private String descriptionAz;
        private String nameEn;
        private String descriptionEn;
        private String nameRu;
        private String descriptionRu;
        private String image;
        private Double price;
        private Boolean isAvailable;
    }
}
