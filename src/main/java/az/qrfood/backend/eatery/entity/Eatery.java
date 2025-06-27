package az.qrfood.backend.eatery.entity;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.category.entity.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single restaurant or eatery in the system.
 * <p>
 * This entity holds all the core information about a restaurant, including its name,
 * address, and contact details. It also serves as the root for its associated data,
 * such as its menu categories, tables, and phone numbers.
 * </p>
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "eatery")
public class Eatery {

    /**
     * The unique identifier for the eatery.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The official name of the eatery.
     */
    private String name;

    /**
     * The physical address of the eatery.
     */
    private String address;

    /**
     * A list of phone numbers associated with the eatery.
     * <p>
     * This is a one-to-many relationship, where one eatery can have multiple phone numbers.
     * The {@code orphanRemoval=true} option ensures that if a phone number is removed
     * from this list, it is also deleted from the database.
     * </p>
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EateryPhone> phones;

    /**
     * A list of all the tables within this eatery.
     * <p>
     * This relationship links the eatery to its physical tables, which are represented
     * by the {@link TableInEatery} entity.
     * </p>
     */
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL)
    private List<TableInEatery> tables;

    /**
     * A list of all menu categories offered by this eatery.
     * <p>
     * This relationship links the eatery to its menu structure, starting with the
     * top-level categories.
     * </p>
     */
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL)
    private List<Category> categories;

    /**
     * The geographical latitude of the eatery.
     * Useful for location-based services and validation.
     */
    private Double geoLat;

    /**
     * The geographical longitude of the eatery.
     * Useful for location-based services and validation.
     */
    private Double geoLng;

    /**
     * Default constructor. Initializes the lists for phones, tables, and categories.
     */
    public Eatery() {
        this.phones = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

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
