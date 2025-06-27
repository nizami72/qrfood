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
 * Mapper component for converting between {@link Order} entities and {@link OrderDto} Data Transfer Objects.
 * <p>
 * This class handles the transformation of order data, including mapping order items
 * and calculating the total price of an order.
 * </p>
 */
@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    /**
     * Constructs an OrderMapper with an OrderItemMapper dependency.
     *
     * @param orderItemMapper The mapper for converting between OrderItem entities and DTOs.
     */
    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Converts an {@link Order} entity to an {@link OrderDto}.
     * <p>
     * This method maps all relevant fields from the entity to the DTO,
     * including mapping its associated order items and calculating the total order price.
     * </p>
     *
     * @param order The {@link Order} entity to convert.
     * @return The converted {@link OrderDto}, or {@code null} if the input order is {@code null}.
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
     * Converts a list of {@link Order} entities to a list of {@link OrderDto}s.
     *
     * @param orders The list of {@link Order} entities to convert.
     * @return A list of converted {@link OrderDto}s, or {@code null} if the input list is {@code null}.
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
     * Converts a list of {@link OrderItem} entities to a list of {@link OrderItemDTO}s.
     *
     * @param items The list of {@link OrderItem} entities to convert.
     * @return A list of converted {@link OrderItemDTO}s, or {@code null} if the input list is {@code null}.
     */
    private List<OrderItemDTO> mapOrderItems(List<OrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total price of an order based on its items.
     * The price is calculated by summing the product of each item's quantity and its price at the time of order.
     *
     * @param orderItems The list of {@link OrderItem}s in the order.
     * @return The total price of the order as a double.
     */
    private double calculatePrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(orderItem -> {
                    return orderItem.getQuantity() * orderItem.getDishEntity().getPrice().doubleValue();
                })
                .sum();
    }

}
