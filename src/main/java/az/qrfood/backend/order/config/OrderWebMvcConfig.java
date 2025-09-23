package az.qrfood.backend.order.config;

import az.qrfood.backend.order.interceptor.OrderControllerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the OrderControllerInterceptor.
 * <p>
 * This class registers the interceptor to validate path parameters for order-related endpoints.
 * </p>
 */
@Configuration
@Log4j2
public class OrderWebMvcConfig implements WebMvcConfigurer {

    private final OrderControllerInterceptor orderControllerInterceptor;

    @Value("${order.id}")
    private String orderIdPath;

    @Value("${order.id.delete}")
    private String orderIdDeletePath;

    @Value("${order.id.put}")
    private String orderIdPutPath;

    @Value("${order.id.add-dishes}")
    private String orderIdAddDishesPath;

    @Value("${order.item.order.id}")
///api/eatery/{eateryId}/order-item/order/{orderId}
    private String orderItemPath;


    public OrderWebMvcConfig(OrderControllerInterceptor orderControllerInterceptor) {
        this.orderControllerInterceptor = orderControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor for order-related endpoints
        log.info("Registering OrderControllerInterceptor for paths: {}, {}, {}, {}, {}",
                orderIdPath, orderIdDeletePath, orderIdPutPath, orderIdAddDishesPath, orderItemPath);
        registry.addInterceptor(orderControllerInterceptor)
                .addPathPatterns(orderIdPath, orderIdDeletePath, orderIdPutPath, orderIdAddDishesPath, orderItemPath);
    }
}
