package az.qrfood.backend.order.interceptor;

import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
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
 * Interceptor for OrderController endpoints to validate path parameters.
 * <p>
 * This interceptor checks that:
 * 1. If eateryId and orderId are specified, the order must belong to the specified eatery
 * </p>
 */
@Log4j2
@Component
public class OrderControllerInterceptor implements HandlerInterceptor {

    private final CustomerOrderRepository orderRepository;

    @Autowired
    public OrderControllerInterceptor(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.debug("OrderControllerInterceptor: Validating path parameters");

        // Get path variables from the request
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables == null) {
            return true; // No path variables to validate
        }

        // Extract path variables
        String eateryIdStr = pathVariables.get("eateryId");
        String orderIdStr = pathVariables.get("orderId");

        // If we have both eateryId and orderId, validate that orderId belongs to eateryId
        if (eateryIdStr != null && orderIdStr != null) {
            Long eateryId = Long.parseLong(eateryIdStr);
            Long orderId = Long.parseLong(orderIdStr);
            
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new NotYourResourceException("Order not found with id: " + orderIdStr);
            }
            
            Order order = orderOpt.get();
            if (!order.getTable().getEatery().getId().equals(eateryId)) {
                throw new NotYourResourceException("Access to resources that do not belong to each other or do not exist: Order " + orderIdStr + " does not belong to Eatery " + eateryIdStr);
            }
        }

        return true;
    }
}