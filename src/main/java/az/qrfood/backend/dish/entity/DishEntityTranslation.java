package az.qrfood.backend.dish.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a translation for a {@link DishEntity}'s name and description in a specific language.
 * <p>
 * This entity supports multi-language menus, allowing dish names and descriptions
 * to be displayed in different languages based on user preference.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish_translation",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"dish_id", "lang"})})
public class DishEntityTranslation extends BaseEntity {

    /**
     * The dish to which this translation belongs.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private DishEntity dishItem;

    /**
     * The language code for this translation (e.g., "en", "ru", "az").
     * This field is part of a unique constraint with `dish_id`.
     */
    @Column(nullable = false, length = 5)
    private String lang; // For example: "en", "ru", "az"

    /**
     * The translated name of the dish in the specified language.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The translated description of the dish in the specified language.
     * This field is optional.
     */
    private String description;
}
