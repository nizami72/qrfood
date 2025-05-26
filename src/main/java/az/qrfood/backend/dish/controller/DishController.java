package az.qrfood.backend.dish.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("${segment.api.dish}")
public class DishController {

    private final DishService dishService;
    private final DishRepository dishRepository;

    public DishController(DishService dishService, DishRepository dishRepository) {
        this.dishService = dishService;
        this.dishRepository = dishRepository;
    }

   /**
     * Retrieve the category dish by its ID.
     */
    @Operation(summary = "Retrieve the restourant dish by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant was not found")
    })
    @GetMapping("/category_id/{categoryId}")
    public ResponseEntity<List<DishDto>> getDishes(@PathVariable Long categoryId) {
        log.debug("Retrieve the category dish by ID");

        List<DishDto> dishInCategory = dishService.getAllDishesInCategory(categoryId);
        return ResponseEntity.ok(dishInCategory);
    }

    /**
     * Create dish item for category.

     * @param dishDto - dish item DTO
     * @return Response entity
     */
    @PostMapping(value = "/category-id/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createDish(@PathVariable("id") Long categoryId,
                                           @RequestPart("data") DishDto dishDto,
                                           @RequestPart("image") MultipartFile file) {
        dishDto.setCategoryId(categoryId);
        log.debug("Request to create dish item: {}", dishDto);
        DishEntity dishEntity = dishService.addDish(dishDto, file);
        return ResponseEntity.ok(dishEntity.getId());
    }

    /**
     * Get dish Item by ID.

     * @param dishId the dish item ID
     * @return dish item dto
     */
    @GetMapping("/{dishId}")
    public DishDto getDishItemById(@PathVariable Long dishId) {
        Optional<DishEntity> dishEntity = dishRepository.findById(dishId);
        if(dishEntity.isEmpty()) {
            throw new EntityNotFoundException("Dish item with id " + dishId + " not found");
        }
        return DishService.convertEntityToDto(dishEntity.get());
    }


    @DeleteMapping("/{dishId}")
    public ResponseEntity<String> deleteDishItemById(@PathVariable Long dishId) {
        log.debug("Request to delete dish item with id {}", dishId);
        dishRepository.deleteById(dishId);
        return  ResponseEntity.ok("Dish item with id " + dishId + " deleted successfully");
    }

}