package az.qrfood.backend.orderitem.config;

import az.qrfood.backend.orderitem.interceptor.OrderItemControllerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the OrderItemControllerInterceptor.
 * <p>
 * This class registers the interceptor to validate path parameters for order-item-related endpoints.
 * </p>
 */
@Configuration
@Log4j2
public class OrderItemWebMvcConfig implements WebMvcConfigurer {

    private final OrderItemControllerInterceptor orderItemControllerInterceptor;

    @Value("${order.item}")
    private String orderItemPath;

    @Value("${order.item.id}")
    private String orderItemIdPath;

    @Value("${order.item.order.id}")
    private String orderItemOrderIdPath;

    public OrderItemWebMvcConfig(OrderItemControllerInterceptor orderItemControllerInterceptor) {
        this.orderItemControllerInterceptor = orderItemControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor for order-item-related endpoints
        log.info("Registering OrderItemControllerInterceptor for paths: {}, {}, {}",
                orderItemPath, orderItemIdPath, orderItemOrderIdPath);
        registry.addInterceptor(orderItemControllerInterceptor)
                .addPathPatterns(orderItemPath, orderItemIdPath, orderItemOrderIdPath);
    }
}