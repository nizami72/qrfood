package az.qrfood.backend.orderitem.service;

import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
import az.qrfood.backend.order.repository.OrderItemRepository;
import az.qrfood.backend.orderitem.mapper.OrderItemMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;
    private OrderItemDTO orderItemDTO;
    private Order order;
    private DishEntity dish;
    private List<DishEntityTranslation> translations;

    @BeforeEach
    void setUp() {
        // Set up test data
        order = new Order();
        order.setId(1L);

        dish = new DishEntity();
        dish.setId(1L);
        dish.setPrice(BigDecimal.valueOf(10.0));

        translations = new ArrayList<>();
        DishEntityTranslation translation = new DishEntityTranslation();
        translation.setName("Test Dish");
        translations.add(translation);
        dish.setTranslations(translations);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setDishEntity(dish);
        orderItem.setQuantity(2);
        orderItem.setNote("Test note");
        orderItem.setPriceAtOrder(BigDecimal.valueOf(10.0));

        orderItemDTO = OrderItemDTO.builder()
                .id(1L)
                .dishItemId(1L)
                .orderItemId(1L)
                .name("Test Dish")
                .quantity(2)
                .note("Test note")
                .build();
    }

    @Test
    void getOrderItemById_shouldReturnOrderItem_whenOrderItemExists() {
        // Arrange
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(orderItem)).thenReturn(orderItemDTO);

        // Act
        OrderItemDTO result = orderItemService.getOrderItemById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Dish", result.getName());
        assertEquals(2, result.getQuantity());
        assertEquals("Test note", result.getNote());
        verify(orderItemRepository).findById(1L);
        verify(orderItemMapper).toDto(orderItem);
    }

    @Test
    void getOrderItemById_shouldThrowException_whenOrderItemDoesNotExist() {
        // Arrange
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.getOrderItemById(999L);
        });
        verify(orderItemRepository).findById(999L);
        verify(orderItemMapper, never()).toDto(any());
    }

    @Test
    void getOrderItemsByOrderId_shouldReturnOrderItems_whenOrderExists() {
        // Arrange
        List<OrderItem> orderItems = List.of(orderItem);
        List<OrderItemDTO> orderItemDTOs = List.of(orderItemDTO);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(orderItems);
        when(orderItemMapper.toDtoList(orderItems)).thenReturn(orderItemDTOs);

        // Act
        List<OrderItemDTO> result = orderItemService.getOrderItemsByOrderId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).findByOrderId(1L);
        verify(orderItemMapper).toDtoList(orderItems);
    }

    @Test
    void getOrderItemsByOrderId_shouldThrowException_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.getOrderItemsByOrderId(999L);
        });
        verify(orderRepository).findById(999L);
        verify(orderItemRepository, never()).findByOrderId(any());
        verify(orderItemMapper, never()).toDtoList(any());
    }

    @Test
    void createOrderItem_shouldCreateOrderItem_whenValidData() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemMapper.toDto(orderItem)).thenReturn(orderItemDTO);

        // Act
        OrderItemDTO result = orderItemService.createOrderItem(orderItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
        verify(dishRepository).findById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderItemMapper).toDto(orderItem);
    }

    @Test
    void updateOrderItem_shouldUpdateOrderItem_whenOrderItemExists() {
        // Arrange
        OrderItemDTO updateDTO = OrderItemDTO.builder()
                .quantity(3)
                .note("Updated note")
                .build();

        OrderItem updatedOrderItem = new OrderItem();
        updatedOrderItem.setId(1L);
        updatedOrderItem.setOrder(order);
        updatedOrderItem.setDishEntity(dish);
        updatedOrderItem.setQuantity(3);
        updatedOrderItem.setNote("Updated note");
        updatedOrderItem.setPriceAtOrder(BigDecimal.valueOf(10.0));

        OrderItemDTO updatedDTO = OrderItemDTO.builder()
                .id(1L)
                .dishItemId(1L)
                .orderItemId(1L)
                .name("Test Dish")
                .quantity(3)
                .note("Updated note")
                .build();

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(updatedOrderItem);
        when(orderItemMapper.toDto(updatedOrderItem)).thenReturn(updatedDTO);

        // Act
        OrderItemDTO result = orderItemService.updateOrderItem(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals("Updated note", result.getNote());
        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderItemMapper).toDto(updatedOrderItem);
    }

    @Test
    void updateOrderItem_shouldThrowException_whenOrderItemDoesNotExist() {
        // Arrange
        OrderItemDTO updateDTO = OrderItemDTO.builder()
                .quantity(3)
                .note("Updated note")
                .build();

        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.updateOrderItem(999L, updateDTO);
        });
        verify(orderItemRepository).findById(999L);
        verify(orderItemRepository, never()).save(any());
        verify(orderItemMapper, never()).toDto(any());
    }

    @Test
    void deleteOrderItem_shouldDeleteOrderItem_whenOrderItemExists() {
        // Arrange
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        doNothing().when(orderItemRepository).delete(orderItem);

        // Act
        orderItemService.deleteOrderItem(1L);

        // Assert
        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).delete(orderItem);
    }

    @Test
    void deleteOrderItem_shouldThrowException_whenOrderItemDoesNotExist() {
        // Arrange
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.deleteOrderItem(999L);
        });
        verify(orderItemRepository).findById(999L);
        verify(orderItemRepository, never()).delete(any());
    }
}
