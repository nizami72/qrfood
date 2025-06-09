package az.qrfood.backend.category.controller;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Log4j2
@RestController
@RequestMapping("${segment.categories}")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET all categories for eatery.

     * @param eateryId the eatery id
     * @return Liast of Categories DTO
     */
    @GetMapping()
    public ResponseEntity<List<CategoryDto>> eateryCategories(@PathVariable(value = "eateryId") Long eateryId) {
        log.debug("Find all categories for eatery {}", eateryId);
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }

    /**
     * GET the category by id.
     *
     * @param categoryId the category id
     * @return CategoryDto
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> categoryById(@PathVariable(value = "id") Long categoryId) {
        log.debug("Find the category by ID {}", categoryId);
        CategoryDto category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    /**
     * POST a new category for the eatery specified.
     *
     * @param eateryId        eatery ID the category is created dfor
     * @param dishCategoryDto category data
     * @return id of created eatery
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createDishCategory(@PathVariable Long eateryId,
                                                   @RequestPart("data") CategoryDto dishCategoryDto,
                                                   @RequestPart("image") MultipartFile file) {
        dishCategoryDto.setEateryId(eateryId);
        log.debug("Create category item: {}", dishCategoryDto);
        Category cid = categoryService.createCategory(dishCategoryDto, file);

        return ResponseEntity.ok(cid.getId());
    }

    /**
     * DELETE the category by its ID
     *
     * @param categoryId category ID
     * @return deleted category ID
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(value = "id") Long categoryId) {
        log.debug("Delete category: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * PUT (update) an existing category.
     *
     * @param categoryId      the category ID to update
     * @param dishCategoryDto updated category data
     * @param file            optional new image file
     * @return updated category ID
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateCategory(@PathVariable(value = "id") Long categoryId,
                                               @RequestPart("data") CategoryDto dishCategoryDto,
                                               @RequestPart(value = "image", required = false) MultipartFile file) {
        log.debug("Update category: {}", categoryId);
        dishCategoryDto.setCategoryId(categoryId);
        Category updatedCategory = categoryService.updateCategory(dishCategoryDto, file);

        return ResponseEntity.ok(updatedCategory.getId());
    }
}
