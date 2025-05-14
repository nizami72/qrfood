package az.qrfood.backend.menu.entity;

import az.qrfood.backend.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu_category")
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "common_category_id")
    private CommonCategory commonCategory;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<MenuItem> items;

    // Helper method to get localized name if common category exists
    public String getLocalizedName(String locale) {
        if (commonCategory != null) {
            return commonCategory.getNameForLocale(locale);
        }
        return name;
    }
}