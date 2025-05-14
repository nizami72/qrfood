package az.qrfood.backend.menu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "common_category")
public class CommonCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String iconUrl;

    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommonCategoryTranslation> translations = new ArrayList<>();

    // Helper method to get translation for a specific locale
    public String getNameForLocale(String locale) {
        return translations.stream()
                .filter(t -> t.getLocale().equals(locale))
                .map(CommonCategoryTranslation::getName)
                .findFirst()
                .orElse(code); // Fallback to code if translation not found
    }
}