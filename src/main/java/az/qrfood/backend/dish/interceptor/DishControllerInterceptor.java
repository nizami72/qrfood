package az.qrfood.backend.dish.interceptor;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
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
 * Interceptor for DishController endpoints to validate path parameters.
 * <p>
 * This interceptor checks that:
 * 1. If categoryId is specified, it must belong to the specified eateryId
 * 2. If dishId is specified, it must belong to the specified categoryId
 * </p>
 */
@Log4j2
@Component
public class DishControllerInterceptor implements HandlerInterceptor {

    private final CategoryService categoryService;

    public DishControllerInterceptor(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.debug("DishControllerInterceptor: Validating path parameters");

        // Get path variables from the request
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables == null) {
            return true; // No path variables to validate
        }

        // Extract path variables
        String eateryIdStr = pathVariables.get("eateryId");
        String categoryIdStr = pathVariables.get("categoryId");
        String dishIdStr = pathVariables.get("dishId");

        // If we have both eateryId and categoryId, validate that categoryId belongs to eateryId
        if (eateryIdStr != null && categoryIdStr != null) {
            Long eateryId = Long.parseLong(eateryIdStr);
            Long categoryId = Long.parseLong(categoryIdStr);
            
            CategoryDto categoryDto = categoryService.findCategoryByEateryIdAndId(eateryId, categoryId);
            if(categoryDto == null) {
                throw new NotYourResourceException("Access to resources that are not belong to each other or does not exist: " + categoryIdStr);
            }

            // If we also have dishId, validate that dishId belongs to categoryId
            if (dishIdStr != null) {
                Long dishId = Long.parseLong(dishIdStr);
                categoryDto.getDishes().stream()
                        .filter(d -> d.getDishId().equals(dishId))
                        .findFirst()
                        .orElseThrow(() -> new NotYourResourceException("Access to resources that are not belong to each other or does not exist: " + dishIdStr));
            }
        }

        return true;
    }
}