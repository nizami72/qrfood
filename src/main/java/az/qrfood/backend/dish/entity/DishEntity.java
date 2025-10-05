package az.qrfood.backend.dish.entity;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.common.entity.BaseEntity;
import az.qrfood.backend.kitchendepartment.entity.KitchenDepartmentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a single dish or menu item within a specific {@link Category}.
 * <p>
 * This entity stores details about a dish, including its price, image,
 * availability, and translations for its name and description.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish")
public class DishEntity extends BaseEntity {

    /**
     * The category to which this dish belongs.
     * This is a many-to-one relationship, indicating that multiple dishes
     * can belong to a single category.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The price of the dish.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * The file name of the image associated with this dish.
     * This is typically used for displaying the dish's photo in the menu.
     */
    private String image;

    /**
     * Indicates whether the dish is currently available.
     * Defaults to {@code true}.
     */
    private boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_department_id")
    private KitchenDepartmentEntity kitchenDepartment;

    /**
     * A list of translations for this dish's name and description.
     * This supports multi-language functionality for the menu items.
     * The {@code cascade = CascadeType.ALL} and {@code orphanRemoval = true} ensure
     * that associated translations are managed along with the dish.
     * The {@code FetchType.LAZY} indicates that translations are loaded only when accessed.
     */
    @OneToMany(mappedBy = "dishItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DishEntityTranslation> translations;
}
