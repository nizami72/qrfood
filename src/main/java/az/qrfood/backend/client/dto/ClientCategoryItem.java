package az.qrfood.backend.client.dto;

import java.math.BigDecimal;

public record ClientCategoryItem(String name, String description, BigDecimal price, String imageUrl) {
}

