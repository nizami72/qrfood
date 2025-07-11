package az.qrfood.backend.dish.service;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.service.StorageService;
import az.qrfood.backend.lang.Language;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.dish.repository.DishRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link DishEntity} entities.
 * <p>
 * This class encapsulates the business logic related to dishes,
 * including CRUD operations, handling dish images, and managing translations.
 * </p>
 */
@Service
@Log4j2
public class DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;

    @Value("${folder.predefined.dish.images}")
    private String appHomeFolderImage;

    /**
     * Constructs a DishService with necessary dependencies.
     *
     * @param dishRepository     The repository for Dish entities.
     * @param categoryRepository The repository for Category entities.
     * @param storageService     The service for handling file storage operations.
     */
    public DishService(DishRepository dishRepository,
                       CategoryRepository categoryRepository, StorageService storageService) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }

    /**
     * Retrieves all dishes for a particular category.
     *
     * @param categoryId The ID of the category.
     * @return A list of {@link DishDto} representing all dishes in the specified category.
     * @throws EntityNotFoundException if the category with the given ID is not found.
     */
    public List<DishDto> getAllDishesInCategory(Long categoryId) {
        Optional<Category> categories = categoryRepository.findById(categoryId);
        if(categories.isEmpty()) {
            throw new EntityNotFoundException("Category with id " + categoryId + " not found");
        }

        List<DishEntity> dishEntities = categories.get().getItems();
        List<DishDto> dishDtos = new ArrayList<>();

        dishEntities.forEach(dishEntity -> {
            dishDtos.add(convertEntityToDto(dishEntity));

        });
        return dishDtos;
    }

    /**
     * Adds a new dish to a category.
     * <p>
     * This method handles the creation of the dish entity, its translations,
     * and the storage of its associated image file.
     * </p>
     *
     * @param dto           The {@link DishDto} containing the dish data.
     * @param multipartFile The image file for the dish.
     * @return The newly created {@link DishEntity}.
     * @throws IllegalArgumentException if the specified category is not found.
     */
    public DishEntity addDish(DishDto dto, MultipartFile multipartFile) {
        Optional<Category> optionalCategory = categoryRepository.findById(dto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }

        DishEntity dishEntity = Util.copyProperties(dto, DishEntity.class);
        dishEntity.setTranslations(new ArrayList<>());
        dishEntity.setCategory(optionalCategory.get());

        dishEntity = dishRepository.save(dishEntity);

        List<DishEntityTranslation> translations = List.of(
                new DishEntityTranslation(dishEntity, Language.az.name(), dto.getNameAz(), dto.getDescriptionAz()),
                new DishEntityTranslation(dishEntity, Language.en.name(), dto.getNameEn(), dto.getDescriptionEn()),
                new DishEntityTranslation(dishEntity, Language.ru.name(), dto.getNameRu(), dto.getDescriptionRu())
        );

        log.debug("Dish Item created [{}]", dishEntity);
        dishEntity.getTranslations().addAll(translations);

        saveImage(multipartFile, dishEntity);

        return dishRepository.save(dishEntity);
    }

    private CategoryDto convertDtoToEntity(DishDto dish) {
        return CategoryDto.builder()
                .eateryId(dish.getCategoryId())
                .nameEn(dish.getNameEn())
                .nameRu(dish.getNameRu())
                .nameAz(dish.getNameAz())
                .build();
    }

    /**
     * Converts a {@link DishEntity} to a {@link DishDto}.
     * <p>
     * This is a static method, allowing it to be called directly without an instance of {@code DishService}.
     * It maps the entity's properties to the DTO, including its associated translations.
     * </p>
     *
     * @param dishEntity The {@link DishEntity} to convert.
     * @return The converted {@link DishDto}.
     */
    public static DishDto convertEntityToDto(DishEntity dishEntity) {
        DishDto dto = DishDto.builder()
                .dishId(dishEntity.getId())
                .categoryId(dishEntity.getCategory().getId())
                .price(dishEntity.getPrice())
                .image(dishEntity.getImage())
                .isAvailable(dishEntity.isAvailable())
                .build();

        dishEntity.getTranslations().forEach(t -> {
            if (t.getLang().equals(Language.az.name())) {
                dto.setNameAz(t.getName());
                dto.setDescriptionAz(t.getDescription());
            } else if (t.getLang().equals(Language.en.name())) {
                dto.setNameEn(t.getName());
                dto.setDescriptionEn(t.getDescription());
            } else if (t.getLang().equals(Language.ru.name())) {
                dto.setNameRu(t.getName());
                dto.setDescriptionRu(t.getDescription());
            }
        });
        return dto;

    }


    /**
     * Deletes a dish from a specific category.
     * <p>
     * This method is transactional. It finds the category and the dish within it,
     * then removes the dish from the category's item list. Due to {@code orphanRemoval=true}
     * on the {@code items} collection in {@link Category}, the dish entity will be
     * automatically deleted from the database when the transaction commits.
     * </p>
     *
     * @param categoryId The ID of the category from which to delete the dish.
     * @param dishId     The ID of the dish to delete.
     * @return A {@link ResponseEntity} with a success message.
     * @throws EntityNotFoundException if the category or the dish within the category is not found.
     */
    @Transactional
    public ResponseEntity<String> deleteDishItemById(Long categoryId, Long dishId) {
        log.debug("Requested to delete dish [{}] from category [{}]", dishId, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found " + categoryId));

        DishEntity dish = category.getItems().stream()
                .filter(item -> item.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Category [%s] does not contain dish [%s]", categoryId, dishId)));

        // Remove from list — this triggers orphanRemoval
        category.getItems().remove(dish);

        // No need to call dishRepository.deleteById()
        // Since orphanRemoval = true, it will be deleted automatically when the transaction commits.

        return ResponseEntity.ok(String.format("Dish [%s] deleted successfully", dishId));
    }

    /**
     * Updates an existing dish within a specific category.
     * <p>
     * This method is transactional. It updates the dish's price, availability,
     * and translations. It also handles updating the dish's image if a new one is provided.
     * </p>
     *
     * @param categoryId    The ID of the category containing the dish.
     * @param dishId        The ID of the dish to update.
     * @param dto           The {@link DishDto} containing the updated dish data.
     * @param multipartFile An optional new image file for the dish.
     * @return The updated {@link DishEntity}.
     * @throws EntityNotFoundException if the category or the dish within the category is not found.
     */
    @Transactional
    public DishEntity updateDish(Long categoryId, Long dishId, DishDto dto, MultipartFile multipartFile) {
        log.debug("Updating dish [{}] in category [{}]", dishId, categoryId);

        // Verify the category exists
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found " + categoryId));

        // Find the dish to update
        DishEntity dishEntity = category.getItems().stream()
                .filter(item -> item.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Category [%s] does not contain dish [%s]", categoryId, dishId)));

        // Update the dish properties
        dishEntity.setPrice(dto.getPrice());
        dishEntity.setAvailable(dto.isAvailable());

        // Update translations
        dishEntity.getTranslations().forEach(translation -> {
            if (translation.getLang().equals(Language.az.name())) {
                translation.setName(dto.getNameAz());
                translation.setDescription(dto.getDescriptionAz());
            } else if (translation.getLang().equals(Language.en.name())) {
                translation.setName(dto.getNameEn());
                translation.setDescription(dto.getDescriptionEn());
            } else if (translation.getLang().equals(Language.ru.name())) {
                translation.setName(dto.getNameRu());
                translation.setDescription(dto.getDescriptionRu());
            }
        });

        saveImage(multipartFile, dishEntity);

        // Save the updated dish
        return dishRepository.save(dishEntity);
    }

    // Update image if provided
    private void saveImage(MultipartFile multipartFile, DishEntity dishEntity) {
        String folder = storageService.createDishesFolder(dishEntity.getId());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = multipartFile.getOriginalFilename();
            storageService.deleteAllAndSaveFile(folder, multipartFile, fileName);
            dishEntity.setImage(fileName);
        } else {
            String fileName = dishEntity.getImage();
            String sourceFile = appHomeFolderImage + fileName;
            storageService.saveFile(folder, sourceFile, fileName);
        }
    }

}
