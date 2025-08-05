package az.qrfood.backend.category.config;

import az.qrfood.backend.category.interceptor.CategoryControllerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the CategoryControllerInterceptor.
 * <p>
 * This class registers the interceptor to validate path parameters for category-related endpoints.
 * </p>
 */
@Configuration
@Log4j2
public class CategoryWebMvcConfig implements WebMvcConfigurer {

    private final CategoryControllerInterceptor categoryControllerInterceptor;

    @Value("${eatery.id.category.id}")
    private String categoryIdPath;

    public CategoryWebMvcConfig(CategoryControllerInterceptor categoryControllerInterceptor) {
        this.categoryControllerInterceptor = categoryControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor for category-related endpoints
        log.info("Registering CategoryControllerInterceptor for path: {}", categoryIdPath);
        registry.addInterceptor(categoryControllerInterceptor)
                .addPathPatterns(categoryIdPath);
    }
}