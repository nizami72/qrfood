package az.qrfood.backend.category.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 5)
    private String lang; // Например: "en", "ru", "az"

    @Column(nullable = false)
    private String name;

    @Override
    public String toString() {
        return "CategoryTranslation{" +
                "id=" + getId() +
                ", lang='" + lang + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
