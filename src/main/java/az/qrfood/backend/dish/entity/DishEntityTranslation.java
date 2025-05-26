package az.qrfood.backend.dish.entity;

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
@Table(name = "dish_translation",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"dish_id", "lang"})})
public class DishEntityTranslation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private DishEntity dishItem;

    @Column(nullable = false, length = 5)
    private String lang; // Например: "en", "ru", "az"

    @Column(nullable = false)
    private String name;

    private String description;
}
