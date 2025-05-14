package az.qrfood.backend.restaurant.entity;

import az.qrfood.backend.table.entity.TableInRestaurant;
import az.qrfood.backend.menu.entity.MenuCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant that uses the QR food ordering system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class Restaurant {

    /**
     * Unique identifier for the restaurant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the restaurant.
     */
    private String name;

    /**
     * Physical address of the restaurant.
     */
    private String address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantPhone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<TableInRestaurant> tables;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<MenuCategory> categories;

    /**
     * Latitude coordinate of the restaurant (for location validation, optional).
     */
    private Double geoLat;

    /**
     * Longitude coordinate of the restaurant (for location validation, optional).
     */
    private Double geoLng;


}
