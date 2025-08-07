package az.qrfood.backend.table.config;

import az.qrfood.backend.table.interceptor.EateryTableRelationshipInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering the EateryTableRelationshipInterceptor.
 * <p>
 * This class registers the interceptor to validate the relationship between eatery and table
 * for specific endpoints defined by the apiClientEateryTable pattern.
 * </p>
 */
@Configuration
@Log4j2
public class TableWebMvcConfig implements WebMvcConfigurer {

    private final EateryTableRelationshipInterceptor eateryTableRelationshipInterceptor;

    @Value("${api.client.eatery.table}")
    private String apiClientEateryTable;

    public TableWebMvcConfig(EateryTableRelationshipInterceptor eateryTableRelationshipInterceptor) {
        this.eateryTableRelationshipInterceptor = eateryTableRelationshipInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor only for paths matching the apiClientEateryTable pattern
        log.info("Registering EateryTableRelationshipInterceptor for path: {}", apiClientEateryTable);
        registry.addInterceptor(eateryTableRelationshipInterceptor)
                .addPathPatterns(apiClientEateryTable);
    }
}