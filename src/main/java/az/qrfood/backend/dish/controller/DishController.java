package az.qrfood.backend.dish.controller;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.dish.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("${api.eatery}")
public class DishController {

    //<editor-fold desc="Fields">
    private final DishService dishService;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    @Value("${category}")
    String category;
    @Value("${dish}")
    String dish;

    //</editor-fold>

    //<editor-fold desc="Constructor">
    public DishController(DishService dishService, DishRepository dishRepository, CategoryRepository categoryRepository) {
        this.dishService = dishService;
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
    }
    //</editor-fold>

    /**
     * Get dish Item by ID in the specified category.
     *
     * @param dishId the dish item ID
     * @return dish item dto
     */
    @Operation(summary = "Get a specific dish by ID within a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Dish or category not found")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'WAITER')")
    @GetMapping("/{eateryId}${category}/{categoryId}${dish}/{dishId}")
    public DishDto getDish(@PathVariable Long categoryId, @PathVariable Long dishId) {
        log.debug("Requested dish [{}] form category [{}]", dishId, categoryId);
        DishEntity d = getDishOrThrow(categoryId, dishId);
        return DishService.convertEntityToDto(d);
    }



    /**
     * GET all dishes in the category.
     *
     * @param categoryId the category ID
     */
    @Operation(summary = "Get all dishes by eatery and category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant was not found")
    })
    @GetMapping("/{eateryId}${category}/{categoryId}${dish}")
    public ResponseEntity<List<DishDto>> getDishes(@PathVariable Long categoryId) {
        log.debug("Retrieve the category dish by ID");

        List<DishDto> dishInCategory = dishService.getAllDishesInCategory(categoryId);
        return ResponseEntity.ok(dishInCategory);
    }


    /**
     * Post dish item for category.
     *
     * @param dishDto - dish item DTO
     * @return Response entity
     */
    @Operation(summary = "Create a new dish in a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PostMapping(value = "/{eateryId}${category}/{categoryId}${dish}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createDish(@PathVariable("categoryId") Long categoryId,
                                           @RequestPart("data") DishDto dishDto,
                                           @RequestPart(value = "image", required = false) MultipartFile file) {
        dishDto.setCategoryId(categoryId);
        log.debug("Request to create dish item: {}", dishDto);
        DishEntity dishEntity = dishService.addDish(dishDto, file);
        return ResponseEntity.ok(dishEntity.getId());
    }

    /**
     * Delete the dish from the category.
     *
     * @param categoryId categoryId wher from the dish is deleted
     * @param dishId dish to be deleted
     * @return response entity
     */
    @Operation(summary = "Delete a dish from a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Dish or category not found")
    })
    @Transactional
    @DeleteMapping("/{eateryId}${category}/{categoryId}${dish}/{dishId}")
    public ResponseEntity<String> deleteDishItemById(@PathVariable Long categoryId, @PathVariable Long dishId) {
        log.debug("Requested to delete dish [{}] from category [{}]", dishId, categoryId);
        return dishService.deleteDishItemById(categoryId,dishId);
    }

    /**
     * Update dish item for category.
     *
     * @param categoryId - category ID
     * @param dishId - dish ID to update
     * @param dishDto - updated dish item DTO
     * @param file - updated image file (optional)
     * @return Response entity with updated dish ID
     */
    @Operation(summary = "Update an existing dish in a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Dish or category not found")
    })
    @PutMapping(value = "/{eateryId}${category}/{categoryId}${dish}/{dishId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateDish(@PathVariable("categoryId") Long categoryId,
                                           @PathVariable("dishId") Long dishId,
                                           @RequestPart("data") DishDto dishDto,
                                           @RequestPart(value = "image", required = false) MultipartFile file) {
        dishDto.setCategoryId(categoryId);
        dishDto.setDishId(dishId);
        log.debug("Request to update dish item: {}", dishDto);
        DishEntity dishEntity = dishService.updateDish(categoryId, dishId, dishDto, file);
        return ResponseEntity.ok(dishEntity.getId());
    }

    /**
     * GET a dish by category id and dish ID

     * @param categoryId the category ID
     * @param dishId the dish ID
     * @return DishEntity
     */

    private DishEntity getDishOrThrow(@PathVariable Long categoryId, @PathVariable Long dishId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()) {
            throw new EntityNotFoundException("Category not found " + categoryId);
        }
        Optional<DishEntity> d = category.get().getItems().stream()
                .filter(cat -> cat.getId().equals(dishId))
                .findFirst();
        if(d.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Category [%s]  doesnt contain dish [%s].", categoryId, dishId));
        }
        return d.get();
    }
}
