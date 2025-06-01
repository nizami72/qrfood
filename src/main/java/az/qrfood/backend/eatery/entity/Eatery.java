package az.qrfood.backend.eatery.entity;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.user.profile.UserProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant that uses the QR food ordering system.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "eatery")
public class Eatery {

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

    public Eatery() {
        this.phones = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EateryPhone> phones;

    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL)
    private List<TableInEatery> tables;

    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL)
    private List<Category> categories;

    /**
     * The user profile that owns this restaurant.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_profile_id")
    private UserProfile owner;

    /**
     * Latitude coordinate of the restaurant (for location validation, optional).
     */
    private Double geoLat;

    /**
     * Longitude coordinate of the restaurant (for location validation, optional).
     */
    private Double geoLng;

    @Override
    public String toString() {
        return "Eatery {\n" +
                "  id=" + id + ",\n" +
                "  name='" + name + "',\n" +
                "  address='" + address + "',\n" +
                "  geoLat=" + geoLat + ",\n" +
                "  geoLng=" + geoLng + ",\n" +
                "  phonesCount=" + (phones != null ? phones.size() : 0) + ",\n" +
                "  tablesCount=" + (tables != null ? tables.size() : 0) + ",\n" +
                "  categoriesCount=" + (categories != null ? categories.size() : 0) + "\n" +
                '}';
    }

}
