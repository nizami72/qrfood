package az.qrfood.backend.category.controller;

import az.qrfood.backend.category.dto.DishCategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category for the eatery specified.

     * @param eateryId eatery ID the category is created dfor
     * @param dishCategoryDto category data
     * @return id of created eatery
     */
    @PostMapping(value = "/create/eatery/{eateryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createDishCategory(@PathVariable Long eateryId,
                                                   @RequestPart("data") DishCategoryDto dishCategoryDto,
                                                   @RequestPart("image") MultipartFile file) {
        dishCategoryDto.setEateryId(eateryId);
        log.debug("Create category item: {}", dishCategoryDto);
        Category id = categoryService.createCategory(dishCategoryDto,file);
        return ResponseEntity.ok(id.getId());
    }

    /**
     * Returns all categories.

     * @return list of DishCategoryDto
     */
    @GetMapping()
    public ResponseEntity<List<DishCategoryDto>> eateryCategories() {
        log.debug("Find all categories");
        List<DishCategoryDto> id = categoryService.findAllCategory();
        return ResponseEntity.ok(id);
    }

    /**
     * Returns all categories for eatery.

     * @param eateryId the eatery id
     * @return Liast of Categories DTO
     */
    @GetMapping(value = "/eatery/{eatery}")
    public ResponseEntity<List<DishCategoryDto>> eateryCategories(@PathVariable(value = "eatery") Long eateryId) {
        log.debug("Find all categories for eatery {}", eateryId);
        List<DishCategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }

    /**
     * Returns the category by its id.

     * @param categoryId the category id
     * @return CategoryDto
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<DishCategoryDto> categoryById(@PathVariable(value = "id") Long categoryId) {
        log.debug("Find the category by ID {}", categoryId);
        DishCategoryDto category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }


    /**
     * Deletes the category by its ID

     * @param categoryId category ID
     * @return deleted category ID
     */
    @DeleteMapping(value="/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(value = "id") Long categoryId) {
        log.debug("Delete category: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }

}