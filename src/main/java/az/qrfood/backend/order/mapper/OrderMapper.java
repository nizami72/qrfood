package az.qrfood.backend.order.mapper;

import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.orderitem.mapper.OrderItemMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Order entities and DTOs.
 */
@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

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
                .orderPrice(calculatePrice(order.getItems()))
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
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private double calculatePrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(orderItem -> {
                    return orderItem.getQuantity() * orderItem.getDishEntity().getPrice().doubleValue();
                })
                .sum();
    }

}
