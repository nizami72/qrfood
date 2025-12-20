package az.qrfood.backend.dish.controller;

import az.qrfood.backend.constant.ApiRoutes;
import az.qrfood.backend.dish.dto.CommonDishCategoryDto;
import az.qrfood.backend.dish.dto.CommonDishDto;
import az.qrfood.backend.dish.service.DishService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.security.access.prepost.PreAuthorize;

@Log4j2
@RestController
@Tag(name = "Common Dish Management", description = "API endpoints for managing predefined common dishes")
public class CommonDishController {

    private final DishService dishService;

    @Value("${app.home.folder}")
    private String appHomeFolder;

    private static final String COMMON_DISHES_FILE = "CommonCategories.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommonDishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * GET predefined dishes from CommonDishes.json file for a specific category.
     *
     * @param categoryName The category ID to get predefined dishes for
     * @return List of predefined CommonDishDto objects for the specified category
     */
    @Operation(summary = "Get predefined dishes for a category", description = "Retrieves a list of predefined dishes for the specified category from CommonDishes.json file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of predefined dishes"),
            @ApiResponse(responseCode = "500", description = "Internal server error or file not found")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping(ApiRoutes.DISH_COMMON_BY_CAT_NAME)
    public ResponseEntity<List<CommonDishDto>> getCommonDishesForCategory(@PathVariable String categoryName) {

        log.debug("Fetching common dishes for category {} from {}", categoryName, appHomeFolder + File.separator + COMMON_DISHES_FILE);

        try {
            File commonDishesFile = new File(appHomeFolder + File.separator + COMMON_DISHES_FILE);

            if (!commonDishesFile.exists()) {
                log.error("CommonDishes.json file not found at {}", commonDishesFile.getAbsolutePath());
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<CommonDishCategoryDto> categories = objectMapper.readValue(
                    commonDishesFile,
                    new TypeReference<>() {
                    }
            );

            Function<CommonDishCategoryDto, String> nameGetter = CommonDishCategoryDto::getNameEn;

            // Find the category that matches the requested categoryId
            List<CommonDishDto> dishes = categories.stream()
                    .filter(category -> nameGetter.apply(category).equals(categoryName))
                    .findFirst()
                    .map(CommonDishCategoryDto::getDishes)
                    .orElse(new ArrayList<>());

            return ResponseEntity.ok(dishes);
        } catch (IOException e) {
            log.error("Error reading CommonDishes.json file", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * POST to create multiple dishes from predefined templates.
     *
     * @param categoryId     The category ID to create dishes in
     * @param selectedDishes List of selected predefined dishes to create
     * @return List of created dish IDs
     */
    @Operation(summary = "Create dishes from predefined templates", description = "Creates multiple dishes from predefined templates for the specified category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created dishes"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN')")
    @PostMapping(ApiRoutes.DISH_COMMON_FROM_TEMPLATE)
    public ResponseEntity<List<Long>> createDishesFromTemplates(
            @PathVariable Long eateryId,
            @PathVariable Long categoryId,
            @RequestBody List<CommonDishDto> selectedDishes) {

        log.debug("Creating [{}] dishes from templates for category [{}]", selectedDishes.size(), categoryId);
        List<Long> createdDishIds = dishService.createDishesFromTemplates(eateryId, categoryId, selectedDishes);
        return ResponseEntity.ok(createdDishIds);
    }
}