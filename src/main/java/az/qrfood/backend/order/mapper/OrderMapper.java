package az.qrfood.backend.order.mapper;

import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Order entities and DTOs.
 */
@Component
public class OrderMapper {

    /**
     * Convert an Order entity to an OrderDTO.
     *
     * @param order the entity to convert
     * @return the DTO
     */
    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDto.builder()
                .id(order.getId())
                .tableId(order.getTable().getId())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .tableNumber(order.getTable().getTableNumber())
                .note(order.getNote())
                .items(mapOrderItems(order.getItems()))
                .build();
    }

    /**
     * Convert a list of Order entities to a list of OrderDTOs.
     *
     * @param orders the entities to convert
     * @return the DTOs
     */
    public List<OrderDto> toDtoList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of OrderItem entities to a list of OrderItemResponseDTOs.
     *
     * @param items the entities to convert
     * @return the DTOs
     */
    private List<OrderItemDTO> mapOrderItems(List<OrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());
    }

    /**
     * Convert an OrderItem entity to an OrderItemDTO.
     *
     * @param item the entity to convert
     * @return the DTO
     */
    private OrderItemDTO mapOrderItem(OrderItem item) {
        if (item == null) {
            return null;
        }

        String dishName = null;
        if (item.getDishEntity() != null && item.getDishEntity().getTranslations() != null && !item.getDishEntity().getTranslations().isEmpty()) {
            // Try to get the first translation
            DishEntityTranslation translation = item.getDishEntity().getTranslations().get(0);
            dishName = translation.getName();
        }

        return OrderItemDTO.builder()
                .id(item.getId())
                .name(dishName)
                .quantity(item.getQuantity())
                .note(item.getNote())
                .dishItemId(item.getId())
                .build();
    }
}
