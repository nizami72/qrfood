package az.qrfood.backend.menu.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu_item_translation",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"menu_item_id", "lang"})})
public class MenuItemTranslation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false, length = 5)
    private String lang; // Например: "en", "ru", "az"

    @Column(nullable = false)
    private String name;

    private String description;
}
