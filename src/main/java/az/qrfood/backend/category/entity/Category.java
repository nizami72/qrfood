package az.qrfood.backend.category.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.eatery.entity.Eatery;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import java.util.Objects;

/**
 * Represents a menu category within a specific eatery.
 * <p>
 * This entity defines a grouping for dishes (e.g., "Appetizers", "Main Courses", "Drinks").
 * Each category belongs to a single eatery and can have multiple dishes associated with it.
 * It also supports multiple language translations for its name and description.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"eatery", "items", "translations"})
@Table(name = "category")
public class Category extends BaseEntity {

    /**
     * The eatery to which this category belongs.
     * The relationship is lazily fetched to optimize performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_id", nullable = false)
    private Eatery eatery;

    /**
     * The file name of the image associated with this category.
     * This is typically used for displaying category icons or banners in the UI.
     */
    private String categoryImageFileName;

    /**
     * Indicates whether the category is currently active and should be displayed on the menu.
     * Defaults to {@code true}.
     */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus categoryStatus;

    /**
     * A list of dishes (items) that belong to this category.
     * This is a one-to-many relationship, where one category can contain multiple dishes.
     * The {@code cascade = CascadeType.ALL} and {@code orphanRemoval = true} ensure
     * that associated dishes are managed (persisted, updated, deleted) along with the category.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DishEntity> items;

    /**
     * A list of translations for this category's name and description.
     * This supports multi-language functionality for the menu categories.
     * The {@code cascade = CascadeType.ALL} and {@code orphanRemoval = true} ensure
     * that associated translations are managed along with the category.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTranslation> translations;

    /**
     * Generates a hash code for the Category object.
     * <p>
     * The hash code is based on the category's translations and the ID of its associated eatery.
     * This method is overridden to ensure proper functioning of collections that rely on hash codes,
     * such as {@code HashMap} and {@code HashSet}.
     * </p>
     *
     * @return The hash code for this Category object.
     * @throws NullPointerException if the associated eatery is null when this method is called,
     *                              as it attempts to access {@code getEatery().getId()}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTranslations(), getEatery().getId());
    }

}
