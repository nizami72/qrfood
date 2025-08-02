package az.qrfood.backend.dto;

import java.util.List;

public record Category(
        String eateryId,
        String nameAz,
        String nameEn,
        String nameRu,
        String image,
        List<Dish> dishes) {
}
