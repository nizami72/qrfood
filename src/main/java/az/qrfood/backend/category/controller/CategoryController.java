package az.qrfood.backend.category.controller;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.dto.CategoryPredefined;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.service.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Category Management", description = "API endpoints for managing food categories in eateries")
public class CategoryController {

    private final CategoryService categoryService;

    @Value("${app.home.folder}")
    private String appHomeFolder;

    private static final String COMMON_CATEGORIES_FILE = "CommonCategories.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET all categories for eatery.

     * @param eateryId the eatery id
     * @return List of Categories DTO
     */
    @Operation(summary = "Get all categories for an eatery", description = "Retrieves a list of all food categories for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${eatery.id.category}")
    public ResponseEntity<List<CategoryDto>> getEateryCategories(@PathVariable(value = "eateryId") Long eateryId) {
        log.debug("Find all categories for eatery [{}]", eateryId);
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }

    /**
     * GET the category by id.
     *
     * @param categoryId the category id
     * @return CategoryDto
     */
    @Operation(summary = "Get a category by ID", description = "Retrieves a specific food category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the category"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER', 'SUPER_ADMIN')")
    @GetMapping(value = "${eatery.id.category.id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable(value = "categoryId") Long categoryId) {
        log.debug("Find the category by ID {}", categoryId);
        CategoryDto category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    /**
     * POST a new category for the eatery specified.
     *
     * @param eateryId        eatery ID the category is created for
     * @param dishCategoryDto category data
     * @return id of created category
     */
    @Operation(summary = "Create a new category", description = "Creates a new food category for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "${eatery.id.category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<Long> createDishCategory(@PathVariable Long eateryId,
                                                   @Valid @RequestPart("data") CategoryDto dishCategoryDto,
                                                   @RequestParam(name="image",required = false) MultipartFile file) {
        dishCategoryDto.setEateryId(eateryId);
        log.debug("Create category item: {}", dishCategoryDto);
        Category cid = categoryService.createCategory(dishCategoryDto, file);

        return ResponseEntity.ok(cid.getId());
    }


    /**
     * POST a new category for the eatery specified.
     *
     * @param eateryId        eatery ID the category is created for
     * @param dishCategoryDto category data
     * @return id of created category
     */
    @Operation(summary = "Create a new category", description = "Creates a new food category for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "${eatery.id.category.predefined}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<Long> createDishCategoryNoImage(@PathVariable Long eateryId,
                                                   @Valid @RequestPart("data") CategoryDto dishCategoryDto)
    {
        dishCategoryDto.setEateryId(eateryId);
        log.debug("Create predefined category item: {}", dishCategoryDto);
        Category cid = categoryService.createCategory(dishCategoryDto, null);

        return ResponseEntity.ok(cid.getId());
    }

    /**
     * DELETE the category by its ID
     *
     * @param categoryId category ID
     * @return deleted category ID
     */
    @Operation(summary = "Delete a category", description = "Deletes a food category with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @DeleteMapping(value = "${eatery.id.category.id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(value = "categoryId") Long categoryId) {
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
    @Operation(summary = "Update an existing category", description = "Updates a food category with the specified ID using the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PutMapping(value = "${eatery.id.category.id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateCategory(@PathVariable(value = "categoryId") Long categoryId,
                                               @Valid @RequestPart("data") CategoryDto dishCategoryDto,
                                               @RequestPart(value = "image", required = false) MultipartFile file) {
        log.debug("Update category: {}", categoryId);
        dishCategoryDto.setCategoryId(categoryId);
        Category updatedCategory = categoryService.updateCategory(dishCategoryDto, file);

        return ResponseEntity.ok(updatedCategory.getId());
    }

    /**
     * GET predefined categories from CommonCategories.json file.
     *
     * @return List of predefined CategoryDto objects
     */
    @Operation(summary = "Get predefined categories", description = "Retrieves a list of predefined categories from CommonCategories.json file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of predefined categories"),
            @ApiResponse(responseCode = "500", description = "Internal server error or file not found")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("/api/category/common")
    public ResponseEntity<List<CategoryPredefined>> getCommonCategories() {
        log.debug("Fetching common categories from {}", appHomeFolder + File.separator + COMMON_CATEGORIES_FILE);

        try {
            File commonCategoriesFile = new File(appHomeFolder + File.separator + COMMON_CATEGORIES_FILE);

            if (!commonCategoriesFile.exists()) {
                log.error("CommonCategories.json file not found at {}", commonCategoriesFile.getAbsolutePath());
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<CategoryPredefined> categories = objectMapper.readValue(
                commonCategoriesFile,
                    new TypeReference<>() {
                    }
            );

            return ResponseEntity.ok(categories);
        } catch (IOException e) {
            log.error("Error reading CommonCategories.json file", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
