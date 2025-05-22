package az.qrfood.backend.category.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.menu.entity.MenuItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "menu_category")
public class Category extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "eatery_id", nullable = false)
    private Eatery eatery;

    private String iconUrl;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<MenuItem> items;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTranslation> translations;

    @Override
    public String toString() {
        return "Category {\n" +
                "  id=" + getId() + ",\n" +
                "  eateryId=" + (eatery != null ? eatery.getId() : "null") + ",\n" +
                "  iconUrl='" + iconUrl + "',\n" +
                "  translationsCount=" + (translations != null ? translations.size() : 0) + ",\n" +
                "  itemsCount=" + (items != null ? items.size() : 0) + "\n" +
                '}';
    }
}
