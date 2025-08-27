package az.qrfood.backend.user.config;

import az.qrfood.backend.user.interceptor.UserControllerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the UserControllerInterceptor.
 * <p>
 * This class registers the interceptor to validate the relationship between eatery and user
 * for specific endpoints defined by the user.id pattern.
 * </p>
 */
@Configuration
@Log4j2
public class UserWebMvcConfig implements WebMvcConfigurer {

    private final UserControllerInterceptor userControllerInterceptor;

    @Value("${user.id}")
    private String userId;

    public UserWebMvcConfig(UserControllerInterceptor userControllerInterceptor) {
        this.userControllerInterceptor = userControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor for paths matching the user.id and user.general patterns
        log.info("Registering UserControllerInterceptor for paths [{}]", userId);
        registry.addInterceptor(userControllerInterceptor)
                .addPathPatterns(userId);
    }
}
