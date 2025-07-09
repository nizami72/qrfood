package az.qrfood.backend.category.service;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.entity.CategoryTranslation;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.service.StorageService;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.lang.Language;
import az.qrfood.backend.dish.service.DishService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing {@link Category} entities.
 * <p>
 * This class encapsulates the business logic related to menu categories,
 * including CRUD operations, handling category images, and managing translations.
 * </p>
 */
@Service
@Log4j2
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EateryRepository eateryRepository;
    private final StorageService storageService;

    @Value("${folder.predefined.category.images}")
    private String appHomeFolder;


    /**
     * Constructs a CategoryService with necessary dependencies.
     *
     * @param categoryRepository The repository for Category entities.
     * @param eateryRepository   The repository for Eatery entities.
     * @param storageService     The service for handling file storage operations.
     */
    public CategoryService(CategoryRepository categoryRepository,
                           EateryRepository eateryRepository, StorageService storageService) {
        this.categoryRepository = categoryRepository;
        this.eateryRepository = eateryRepository;
        this.storageService = storageService;
    }

    /**
     * Creates a new category for a specific eatery.
     * <p>
     * This method handles the creation of the category entity, its translations,
     * and the storage of its associated image file. It also checks for existing
     * categories with the same hash to prevent duplicates.
     * </p>
     *
     * @param dishCategoryDto The DTO containing the category data (name, eatery ID, etc.).
     * @param multipartFile   The image file for the category icon.
     * @return The newly created or existing {@link Category} entity.
     * @throws EntityNotFoundException if the specified eatery does not exist.
     */
    public Category createCategory(CategoryDto dishCategoryDto, MultipartFile multipartFile) {

        Long eateryId = dishCategoryDto.getEateryId();

        // check if an eatery exists
        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "Cant create category for eatery %s, eatery not found", eateryId));
        }

        Category category = Category.builder()
                .eatery(eateryOp.get())
                .build();

        category.setCategoryImageFileName(Util.generateFileName() + ".webp");

        List<CategoryTranslation> categoryTranslations = List.of(
                new CategoryTranslation(category, Language.az.name(), dishCategoryDto.getNameAz()),
                new CategoryTranslation(category, Language.en.name(), dishCategoryDto.getNameEn()),
                new CategoryTranslation(category, Language.ru.name(), dishCategoryDto.getNameRu())
        );

        category.setTranslations(categoryTranslations);
        category.setHash(category.hashCode());
        Optional<Category> mayExistCategory = categoryRepository.findByHash(category.hashCode());
        if (mayExistCategory.isPresent()) {
            Category c = mayExistCategory.get();
            log.debug("The category with the same eatery ID and translation already exists, id [{}]", c.getId());
            return c;
        }
        categoryRepository.save(category);
        log.debug("Dish category created [{}]", category);

        String folderPath = storageService.createCategoryFolder(category.getId());
        String fileName = category.getCategoryImageFileName();
        String sourceFile = null;
        if (folderPath != null && multipartFile != null) {
            storageService.saveFile(folderPath, multipartFile, fileName);
            log.info("Dish file [{}] created at dir [{}]", fileName, folderPath);
        } else if (!dishCategoryDto.getImage().isEmpty()) {
            String imageName = dishCategoryDto.getImage();
            sourceFile = appHomeFolder + imageName;
            storageService.saveFile(folderPath, sourceFile, fileName);
            log.debug("Assign predefined image file [{}]", sourceFile);
        } else {
            log.error("Dish file [{}] was not created", sourceFile);
        }

        return category;// what to return id,id, dto or entity
    }

    /**
     * Finds a category by its unique identifier and converts it to a DTO.
     *
     * @param id The ID of the category to find.
     * @return A {@link CategoryDto} representing the found category.
     * @throws EntityNotFoundException if the category with the given ID is not found.
     */
    public CategoryDto findCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {throw new EntityNotFoundException(
                String.format("The category with id [%s] not fount", id));}
        return convertDishCategoryToDto(category.get());
    }

    /**
     * Finds all categories associated with a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link CategoryDto} representing the categories for the specified eatery.
     * @throws EntityNotFoundException if the eatery with the given ID is not found.
     */
    public List<CategoryDto> findAllCategoryForEatery(long eateryId) {

        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {throw new EntityNotFoundException("Eatery not found"); }

        List<Category> categories = eateryOp.get().getCategories();
        if (categories.isEmpty()) {
            String error = String.format("Eatery [%s] has no any category and dish", eateryId);
            log.warn(error);
            return List.of();
        }
        return convertDishCategoryToDto(categories);
    }

    /**
     * Converts a list of {@link Category} entities to a list of {@link CategoryDto}s.
     *
     * @param categories The list of Category entities to convert.
     * @return A list of converted Category DTOs.
     */
    private List<CategoryDto> convertDishCategoryToDto(List<Category> categories) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            categoryDtoList.add(convertDishCategoryToDto(category));
        }
        return categoryDtoList;
    }

    /**
     * Converts a single {@link Category} entity to a {@link CategoryDto}.
     * <p>
     * This method maps the entity's properties to the DTO, including its associated
     * dishes and translations for different languages.
     * </p>
     *
     * @param category The Category entity to convert.
     * @return The converted Category DTO.
     */
    private CategoryDto convertDishCategoryToDto(Category category) {

            CategoryDto dto = new CategoryDto();
            dto.setEateryId(category.getEatery().getId());
            dto.setDishes(category.getItems().stream()
                    .map(DishService::convertEntityToDto)
                    .collect(Collectors.toList())
            );
            dto.setCategoryId(category.getId());
            dto.setImage(category.getCategoryImageFileName());

            category.getTranslations().forEach(t -> {
                if (t.getLang().equals(Language.az.name())) {
                    dto.setNameAz(t.getName());
                } else if (t.getLang().equals(Language.en.name())) {
                    dto.setNameEn(t.getName());
                } else if (t.getLang().equals(Language.ru.name())) {
                    dto.setNameRu(t.getName());
                }
            });

        return dto;
    }

    /**
     * Deletes a category by its unique identifier.
     *
     * @param categoryId The ID of the category to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    public ResponseEntity<String> deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(String.format("Category [%s] deleted successfully", categoryId));
    }

    /**
     * Updates an existing category with new data.
     * <p>
     * This method updates the category's translations, and optionally its image.
     * It also recalculates the category's hash.
     * </p>
     *
     * @param categoryDto   The DTO containing the updated category data.
     * @param multipartFile An optional new image file for the category.
     * @return The updated {@link Category} entity.
     * @throws EntityNotFoundException if the category to update is not found.
     */
    public Category updateCategory(CategoryDto categoryDto, MultipartFile multipartFile) {
        Long categoryId = categoryDto.getCategoryId();

        // Check if category exists
        Optional<Category> categoryOp = categoryRepository.findById(categoryId);
        if (categoryOp.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "Cannot update category %s, category not found", categoryId));
        }

        Category category = categoryOp.get();

        // Update translations
        for (CategoryTranslation translation : category.getTranslations()) {
            if (translation.getLang().equals(Language.az.name())) {
                translation.setName(categoryDto.getNameAz());
            } else if (translation.getLang().equals(Language.en.name())) {
                translation.setName(categoryDto.getNameEn());
            } else if (translation.getLang().equals(Language.ru.name())) {
                translation.setName(categoryDto.getNameRu());
            }
        }

        // Update image if provided
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // Generate a new filename for the image
            String newFileName = Util.generateFileName() + ".webp";
            category.setCategoryImageFileName(newFileName);

            // Save the new image
            String folderPath = storageService.createCategoryFolder(category.getId());
            if (folderPath != null) {
                storageService.saveFile(folderPath, multipartFile, newFileName);
                log.info("Updated category image [{}] saved at dir [{}]", newFileName, folderPath);
            } else {
                log.error("Category image [{}] was not updated", newFileName);
            }
        }

        // Update hash
        category.setHash(category.hashCode());

        // Save updated category
        categoryRepository.save(category);
        log.debug("Category updated [{}]", category);

        return category;
    }
}
