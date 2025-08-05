package az.qrfood.backend.category.interceptor;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Interceptor for CategoryController endpoints to validate path parameters.
 * <p>
 * This interceptor checks that:
 * If both eateryId and categoryId are specified, the category must belong to the specified eatery
 * </p>
 */
@Log4j2
@Component
public class CategoryControllerInterceptor implements HandlerInterceptor {

    private final CategoryService categoryService;

    public CategoryControllerInterceptor(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.debug("CategoryControllerInterceptor: Validating path parameters");

        // Get path variables from the request
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables == null) {
            return true; // No path variables to validate
        }

        // Extract path variables
        String eateryIdStr = pathVariables.get("eateryId");
        String categoryIdStr = pathVariables.get("categoryId");

        // If we have both eateryId and categoryId, validate that categoryId belongs to eateryId
        if (eateryIdStr != null && categoryIdStr != null) {
            Long eateryId = Long.parseLong(eateryIdStr);
            Long categoryId = Long.parseLong(categoryIdStr);
            
            try {
                CategoryDto categoryDto = categoryService.findCategoryByEateryIdAndId(eateryId, categoryId);
                if(categoryDto == null) {
                    throw new NotYourResourceException("Access to resources that are not belong to each other or does not exist: " + categoryIdStr);
                }
            } catch (EntityNotFoundException e) {
                throw new NotYourResourceException("Access to resources that are not belong to each other or does not exist: " + categoryIdStr);
            }
        }

        return true;
    }
}