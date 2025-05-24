package az.qrfood.backend.client.dto;

import java.util.List;

public record ClientCategory(String name, List<ClientCategoryItem> clientCategoryItemsList) {
}
