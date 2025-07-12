package az.qrfood.backend.dish.controller;

import az.qrfood.backend.dish.dto.CommonDishCategoryDto;
import az.qrfood.backend.dish.dto.CommonDishDto;
import az.qrfood.backend.dish.dto.DishDto;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    @GetMapping("/api/dish/common/{categoryName}")
    public ResponseEntity<List<CommonDishDto>> getCommonDishesForCategory(
            @PathVariable String categoryName,
            @CookieValue(value = "language", defaultValue = "en") String lang) {

        log.debug("Fetching common dishes for category {} from {}", categoryName, appHomeFolder + File.separator + COMMON_DISHES_FILE);

        try {
            File commonDishesFile = new File(appHomeFolder + File.separator + COMMON_DISHES_FILE);

            if (!commonDishesFile.exists()) {
                log.error("CommonDishes.json file not found at {}", commonDishesFile.getAbsolutePath());
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<CommonDishCategoryDto> categories = objectMapper.readValue(
                    commonDishesFile,
                    new TypeReference<List<CommonDishCategoryDto>>() {
                    }
            );

            Function<CommonDishCategoryDto, String> nameGetter;

            switch (lang) {
                case "az" -> nameGetter = CommonDishCategoryDto::getNameAz;
                case "ru" -> nameGetter = CommonDishCategoryDto::getNameRu;
                case "en" -> nameGetter = CommonDishCategoryDto::getNameEn;
                default -> nameGetter = CommonDishCategoryDto::getNameEn; // fallback
            }

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
    @PostMapping("/api/dish/common/{categoryId}")
    public ResponseEntity<List<Long>> createDishesFromTemplates(
            @PathVariable Long categoryId,
            @RequestBody List<CommonDishDto> selectedDishes) {

        log.debug("Creating {} dishes from templates for category {}", selectedDishes.size(), categoryId);

        List<Long> createdDishIds = new ArrayList<>();

        for (CommonDishDto template : selectedDishes) {
            // Convert CommonDishDto to DishDto
            DishDto dishDto = DishDto.builder()
                    .categoryId(categoryId)
                    .nameAz(template.getNameAz())
                    .nameEn(template.getNameEn())
                    .nameRu(template.getNameRu())
                    .descriptionAz(template.getDescriptionAz())
                    .descriptionEn(template.getDescriptionEn())
                    .descriptionRu(template.getDescriptionRu())
                    .price(template.getPrice() != null ? template.getPrice() : BigDecimal.ZERO)
                    .image(template.getImage())
                    .isAvailable(true)
                    .build();

            try {
                // Create the dish using the existing service
                var createdDish = dishService.addDish(dishDto, null);
                createdDishIds.add(createdDish.getId());
            } catch (Exception e) {
                log.error("Error creating dish from template: {}", template, e);
                // Continue with the next dish even if one fails
            }
        }

        return ResponseEntity.ok(createdDishIds);
    }
}