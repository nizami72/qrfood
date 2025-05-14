package az.qrfood.backend.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommonCategoryDTO {
    Long id;
    String code;
    String nameForLocale;
    String iconUrl;
}