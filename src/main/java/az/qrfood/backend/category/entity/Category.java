package az.qrfood.backend.category.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.dish.entity.DishEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class Category extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "eatery_id", nullable = false)
    private Eatery eatery;

    private String categoryImageFileName;

    @Column(name = "hash")
    private int hash;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<DishEntity> items;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTranslation> translations;


    @Override
    public String toString() {
        return "Category {\n" +
                "  id=" + getId() + ",\n" +
                "  eateryId=" + (eatery != null ? eatery.getId() : "null") + ",\n" +
                "  iconUrl='" + categoryImageFileName + "',\n" +
                "  translationsCount=" + (translations != null ? translations.size() : 0) + ",\n" +
                "  itemsCount=" + (items != null ? items.size() : 0) + "\n" +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTranslations(), getEatery().getId());
    }

}
