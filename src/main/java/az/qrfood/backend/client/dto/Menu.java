package az.qrfood.backend.client.dto;

import az.qrfood.backend.category.dto.CategoryDto;
import java.util.List;

public record Menu(long eateryId, long tableId, String eateryName, String tableName, List<CategoryDto> categories) {
}
