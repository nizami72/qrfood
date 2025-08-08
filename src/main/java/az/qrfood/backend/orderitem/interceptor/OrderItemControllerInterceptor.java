package az.qrfood.backend.orderitem.interceptor;

import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.repository.OrderItemRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Interceptor for OrderItemController endpoints to validate path parameters.
 * <p>
 * This interceptor checks that:
 * 1. If eateryId and orderItemId are specified, the order item must belong to the specified eatery
 * </p>
 */
@Log4j2
@Component
public class OrderItemControllerInterceptor implements HandlerInterceptor {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemControllerInterceptor(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.debug("OrderItemControllerInterceptor: Validating path parameters");

        // Get path variables from the request
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables == null) {
            return true; // No path variables to validate
        }

        // Extract path variables
        String eateryIdStr = pathVariables.get("eateryId");
        String orderItemIdStr = pathVariables.get("orderItemId");

        // If we have both eateryId and orderItemId, validate that orderItemId belongs to eateryId
        if (eateryIdStr != null && orderItemIdStr != null) {
            Long eateryId = Long.parseLong(eateryIdStr);
            Long orderItemId = Long.parseLong(orderItemIdStr);
            
            Optional<OrderItem> orderItemOpt = orderItemRepository.findById(orderItemId);
            if (orderItemOpt.isEmpty()) {
                throw new NotYourResourceException("Order item not found with id: " + orderItemIdStr);
            }
            
            OrderItem orderItem = orderItemOpt.get();
            Long itemEateryId = orderItem.getOrder().getTable().getEatery().getId();
            
            if (!itemEateryId.equals(eateryId)) {
                throw new NotYourResourceException("Access to resources that do not belong to each other or do not exist: Order item " + orderItemIdStr + " does not belong to Eatery " + eateryIdStr);
            }
        }

        return true;
    }
}