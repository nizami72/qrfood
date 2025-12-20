package az.qrfood.backend.dto;

public record Dish(String nameAz,
                   String descriptionAz,
                   String nameEn,
                   String descriptionEn,
                   String nameRu,
                   String descriptionRu,
                   String image,
                   double price,
                   boolean available) {

}
