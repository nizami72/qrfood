package az.qrfood.backend.category.controller;

import az.qrfood.backend.category.dto.MenuCategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/create/eatery/{eateryId}", consumes = "application/json")
    public ResponseEntity<Long> createMenuCategory(@PathVariable Long eateryId, @RequestBody MenuCategoryDto menuCategoryDto) {
        menuCategoryDto.setEateryId(eateryId);
        log.debug("Create category item: {}", menuCategoryDto);
        Category id = categoryService.createCategory(menuCategoryDto);
        return ResponseEntity.ok(id.getId());
    }

    /**
     * Returns all categories.

     * @return list of MenuCategoryDto
     */
    @GetMapping(value = "/categories")
    public ResponseEntity<List<MenuCategoryDto>> findAllMenuCategory() {
        log.debug("Find all categories");
        List<MenuCategoryDto> id = categoryService.findAllCategory();
        return ResponseEntity.ok(id);
    }

    @GetMapping(value = "/categories/by-eatery")
    public ResponseEntity<List<MenuCategoryDto>> findAllMenuCategory(@RequestParam(value = "eateryId") Long eateryId) {
        log.debug("Find all categories fir eatery {}", eateryId);
        List<MenuCategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<MenuCategoryDto> getCategoryById(@RequestParam(value = "category-id") Long categoryId) {
        log.debug("Find the category by ID {}", categoryId);
        MenuCategoryDto category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping(value="/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        log.debug("Delete category: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }


}