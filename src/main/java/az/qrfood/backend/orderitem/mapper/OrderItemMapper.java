package az.qrfood.backend.orderitem.mapper;

import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting between {@link OrderItem} entities and {@link OrderItemDTO} Data Transfer Objects.
 * <p>
 * This class handles the transformation of order item data, including extracting
 * dish names from translations and mapping other relevant fields.
 * </p>
 */
@Component
public class OrderItemMapper {

    /**
     * Converts an {@link OrderItem} entity to an {@link OrderItemDTO}.
     * <p>
     * This method maps all relevant fields from the entity to the DTO,
     * including extracting the dish name from its translations.
     * </p>
     *
     * @param orderItem The {@link OrderItem} entity to convert.
     * @return The converted {@link OrderItemDTO}, or {@code null} if the input order item is {@code null}.
     */
    public OrderItemDTO toDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        String dishName = null;
        if (orderItem.getDishEntity() != null
                && orderItem.getDishEntity().getTranslations() != null
                && !orderItem.getDishEntity().getTranslations().isEmpty()) {
            // Try to get the first translation
            DishEntityTranslation translation = orderItem.getDishEntity().getTranslations().get(0);
            dishName = translation.getName();
        }

        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .dishId(orderItem.getDishEntity().getId())
                .orderItemId(orderItem.getOrder().getId())
                .name(dishName)
                .quantity(orderItem.getQuantity())
                .note(orderItem.getNote())
                .price(orderItem.getDishEntity().getPrice())
                .build();
    }

    /**
     * Converts a list of {@link OrderItem} entities to a list of {@link OrderItemDTO}s.
     *
     * @param orderItems The list of {@link OrderItem} entities to convert.
     * @return A list of converted {@link OrderItemDTO}s, or {@code null} if the input list is {@code null}.
     */
    public List<OrderItemDTO> toDtoList(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }

        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
