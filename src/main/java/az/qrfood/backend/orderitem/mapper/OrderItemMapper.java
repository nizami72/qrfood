package az.qrfood.backend.orderitem.mapper;

import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between OrderItem entities and DTOs.
 */
@Component
public class OrderItemMapper {

    /**
     * Convert an OrderItem entity to an OrderItemDTO.
     *
     * @param orderItem the entity to convert
     * @return the DTO
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
     * Convert a list of OrderItem entities to a list of OrderItemDTOs.
     *
     * @param orderItems the entities to convert
     * @return the DTOs
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
