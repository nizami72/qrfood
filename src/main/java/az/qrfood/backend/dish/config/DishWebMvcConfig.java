package az.qrfood.backend.dish.config;

import az.qrfood.backend.dish.interceptor.DishControllerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the DishControllerInterceptor.
 * <p>
 * This class registers the interceptor to validate path parameters for dish-related endpoints.
 * </p>
 */
@Configuration
@Log4j2
public class DishWebMvcConfig implements WebMvcConfigurer {

    private final DishControllerInterceptor dishControllerInterceptor;

    @Value("${eatery.id.category.id.dish}")
    private String dishPath;

    @Value("${eatery.id.category.id.dish.id}")
    private String dishIdPath;

    public DishWebMvcConfig(DishControllerInterceptor dishControllerInterceptor) {
        this.dishControllerInterceptor = dishControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor for dish-related endpoints
        log.info("Registering DishControllerInterceptor for paths: {}, {}", dishPath, dishIdPath);
        registry.addInterceptor(dishControllerInterceptor)
                .addPathPatterns(dishPath, dishIdPath);
    }
}
