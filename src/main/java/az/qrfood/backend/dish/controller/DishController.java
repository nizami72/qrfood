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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("${segment.dishes}")
public class DishController {

    //<editor-fold desc="Fields">
    private final DishService dishService;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
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

     * @param dishId the dish item ID
     * @return dish item dto
     */
    @GetMapping("/{dishId}")
    public DishDto getDish(@PathVariable Long categoryId, @PathVariable Long dishId) {
        log.debug("Requested dish [{}] form category [{}]", dishId, categoryId);
        DishEntity d = getDishOrThrow(categoryId, dishId);
        return DishService.convertEntityToDto(d);
    }



    /**
     * Retrieve dishes in the category.
     */
    @Operation(summary = "Retrieve the restourant dish by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant was not found")
    })
    @GetMapping()
    public ResponseEntity<List<DishDto>> getDishes(@PathVariable Long categoryId) {
        log.debug("Retrieve the category dish by ID");

        List<DishDto> dishInCategory = dishService.getAllDishesInCategory(categoryId);
        return ResponseEntity.ok(dishInCategory);
    }


    /**
     * Post dish item for category.

     * @param dishDto - dish item DTO
     * @return Response entity
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createDish(@PathVariable("categoryId") Long categoryId,
                                           @RequestPart("data") DishDto dishDto,
                                           @RequestPart("image") MultipartFile file) {
        dishDto.setCategoryId(categoryId);
        log.debug("Request to create dish item: {}", dishDto);
        DishEntity dishEntity = dishService.addDish(dishDto, file);
        return ResponseEntity.ok(dishEntity.getId());
    }

    /**
     * Delete the dish from the category.

     * @param categoryId categoryId wher from the dish is deleted
     * @param dishId dish to be deleted
     * @return response entity
     */
    @Transactional
    @DeleteMapping("/{dishId}")
    public ResponseEntity<String> deleteDishItemById(@PathVariable Long categoryId, @PathVariable Long dishId) {
        log.debug("Requested to delete dish [{}] from category [{}]", dishId, categoryId);
        return dishService.deleteDishItemById(categoryId,dishId);
    }


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