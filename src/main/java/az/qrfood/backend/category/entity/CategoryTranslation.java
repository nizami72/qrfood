package az.qrfood.backend.category.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

/**
 * Represents a translation for a {@link Category}'s name in a specific language.
 * <p>
 * This entity is used to support multi-language menus, allowing category names
 * to be displayed in different languages based on user preference.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category_translation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category_id", "lang"})
})
public class CategoryTranslation extends BaseEntity {

    /**
     * The category to which this translation belongs.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The language code for this translation (e.g., "en", "ru", "az").
     * This field is part of a unique constraint with `category_id`.
     */
    @Column(nullable = false, length = 5)
    private String lang; // For example: "en", "ru", "az"

    /**
     * The translated name of the category in the specified language.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Provides a string representation of the CategoryTranslation object.
     *
     * @return A formatted string including the ID, language, and translated name.
     */
    @Override
    public String toString() {
        return "CategoryTranslation{" +
                "id=" + getId() +
                ", lang='" + lang + "'" +
                ", name='" + name + "'" +
                '}';
    }

    /**
     * Generates a hash code for the CategoryTranslation object.
     * <p>
     * The hash code is based on the language and the translated name,
     * ensuring that two translations with the same language and name have the same hash.
     * </p>
     *
     * @return The hash code for this CategoryTranslation object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(lang, name);
    }

    /**
     * Compares this CategoryTranslation object with another object for equality.
     * <p>
     * Two CategoryTranslation objects are considered equal if they have the same
     * language and translated name.
     * </p>
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryTranslation)) return false;
        CategoryTranslation that = (CategoryTranslation) o;
        return Objects.equals(lang, that.lang) &&
                Objects.equals(name, that.name);
    }
}
