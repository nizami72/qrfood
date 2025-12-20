package az.qrfood.backend.dish.service;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.service.StorageService;
import az.qrfood.backend.dish.dto.CommonDishDto;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.entity.DishEntityTranslation;
import az.qrfood.backend.dish.entity.DishStatus;
import az.qrfood.backend.dish.exception.QrFoodDataIntegrityViolation;
import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.service.EateryLifecycleService;
import az.qrfood.backend.kitchendepartment.entity.KitchenDepartmentEntity;
import az.qrfood.backend.kitchendepartment.repository.KitchenDepartmentRepository;
import az.qrfood.backend.lang.Language;
import az.qrfood.backend.order.repository.OrderItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //<editor-fold desc="Fields">
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final OrderItemRepository orderItemRepository;
    private final KitchenDepartmentRepository kitchenDepartmentRepository;
    private final EateryLifecycleService eateryLifecycleService;
    @Value("${folder.predefined.dish.images}")
    private String appHomeFolderImage;
    @Value("${default.dish.image}")
    private String defaultDishImage;
    //</editor-fold>

    /**
     * Constructs a DishService with necessary dependencies.
     *
     * @param dishRepository      The repository for Dish entities.
     * @param categoryRepository  The repository for Category entities.
     * @param storageService      The service for handling file storage operations.
     * @param orderItemRepository The repository for OrderItem entities.
     */
    public DishService(DishRepository dishRepository,
                       CategoryRepository categoryRepository,
                       StorageService storageService,
                       OrderItemRepository orderItemRepository,
                       KitchenDepartmentRepository kitchenDepartmentRepository, EateryLifecycleService eateryLifecycleService) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
        this.orderItemRepository = orderItemRepository;
        this.kitchenDepartmentRepository = kitchenDepartmentRepository;
        this.eateryLifecycleService = eateryLifecycleService;
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
        if (categories.isEmpty()) {
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

        // Set kitchen department if provided
        if (dto.getKitchenDepartmentId() != null) {
            KitchenDepartmentEntity kd = kitchenDepartmentRepository.findById(dto.getKitchenDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Kitchen Department not found: " + dto.getKitchenDepartmentId()));
            dishEntity.setKitchenDepartment(kd);
        } else {
            dishEntity.setKitchenDepartment(null);
        }
        dishEntity.setDishStatus(DishStatus.AVAILABLE);
        dishEntity = dishRepository.save(dishEntity);

        List<DishEntityTranslation> translations = List.of(
                new DishEntityTranslation(dishEntity, Language.az.name(), dto.getNameAz(), dto.getDescriptionAz()),
                new DishEntityTranslation(dishEntity, Language.en.name(), dto.getNameEn(), dto.getDescriptionEn()),
                new DishEntityTranslation(dishEntity, Language.ru.name(), dto.getNameRu(), dto.getDescriptionRu())
        );

        log.debug("Dish Item created [{}]", dishEntity);
        dishEntity.getTranslations().addAll(translations);

        saveImage(multipartFile, dishEntity);
        Eatery eatery = dishEntity.getCategory().getEatery();
        if (eatery.getOnboardingStatus() != OnboardingStatus.DISH_CREATED) {
            eateryLifecycleService.tryPromoteStatus(eatery.getId(), OnboardingStatus.DISH_CREATED);
        }
        return dishRepository.save(dishEntity);
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
                .kitchenDepartmentId(dishEntity.getKitchenDepartment() != null ? dishEntity.getKitchenDepartment().getId() : null)
                .price(dishEntity.getPrice())
                .image(dishEntity.getImage())
                .available(dishEntity.getDishStatus().equals(DishStatus.AVAILABLE))
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
     * This method is transactional. It first checks if the dish is referenced by any order items.
     * If it is, a DataIntegrityViolationException is thrown with a meaningful message.
     * Otherwise, it finds the category and the dish within it, then removes the dish from the
     * category's item list. Due to {@code orphanRemoval=true} on the {@code items} collection
     * in {@link Category}, the dish entity will be automatically deleted from the database
     * when the transaction commits.
     * </p>
     *
     * @param categoryId The ID of the category from which to delete the dish.
     * @param dishId     The ID of the dish to delete.
     * @return A {@link ResponseEntity} with a success message.
     * @throws EntityNotFoundException         if the category or the dish within the category is not found.
     * @throws DataIntegrityViolationException if the dish is referenced by any order items.
     */
    @Transactional
    public ResponseEntity<String> deleteDishItemById(Long categoryId, Long dishId) {
        log.debug("Requested to delete dish [{}] from category [{}]", dishId, categoryId);

//         Check if the dish is referenced by any order items
        if (orderItemRepository.existsByDishEntityId(dishId)) {
            log.warn("Cannot delete dish [{}] because it is referenced by order items", dishId);
            throw new QrFoodDataIntegrityViolation(
                    String.format("Cannot delete dish [%s] because it is referenced by order items. " +
                            "Please delete the associated order items first or mark the dish as unavailable instead.", dishId));
        }

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
     * Updates the status of a dish identified by its ID.
     *
     * @param dishId the unique identifier of the dish whose status needs to be updated
     * @param status the new status to be applied to the dish
     * @return a ResponseEntity containing a confirmation message indicating the dish ID and
     * that the status has been successfully updated
     */
    public ResponseEntity<String> updateDishStatus(Long dishId, DishStatus status) {
        DishEntity dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Dish not found with id: " + dishId));

        dish.setDishStatus(status);
        dishRepository.save(dish);
        return ResponseEntity.ok(String.format("Dish [%s] status changed", dishId));
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
        if (dto.isAvailable()) {
            dishEntity.setDishStatus(DishStatus.AVAILABLE);
        } else {
            dishEntity.setDishStatus(DishStatus.OUT_OF_STOCK);
        }

        // Update kitchen department
        if (dto.getKitchenDepartmentId() != null) {
            KitchenDepartmentEntity kd = kitchenDepartmentRepository.findById(dto.getKitchenDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Kitchen Department not found: " + dto.getKitchenDepartmentId()));
            dishEntity.setKitchenDepartment(kd);
        } else {
            dishEntity.setKitchenDepartment(null);
        }

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

        if (multipartFile != null && !multipartFile.isEmpty()) {
            saveImage(multipartFile, dishEntity);
        }

        // Save the updated dish
        return dishRepository.save(dishEntity);
    }

    // Update image if provided
    private void saveImage(MultipartFile multipartFile, DishEntity dishEntity) {
        String folder = storageService.createDishesFolder(dishEntity.getCategory().getEatery().getId(),
                dishEntity.getId());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = Util.generateFileName() + Objects.requireNonNull(
                    multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename()
                    .lastIndexOf('.'));

            storageService.deleteAllAndSaveFile(folder, multipartFile, fileName);
            dishEntity.setImage(fileName);
        } else {
            if (dishEntity.getImage() == null) dishEntity.setImage(defaultDishImage);
            String fileName = dishEntity.getImage();
            String sourceFile = appHomeFolderImage + fileName;
            if (!new File(sourceFile).exists()) {
                dishEntity.setImage(defaultDishImage);
                log.warn("The file [{}] doesnt exists", sourceFile);
            } else {
                storageService.saveFile(folder, sourceFile, fileName);
            }
        }
    }


    @Transactional
    public List<Long> createDishesFromTemplates(Long eateryId, Long categoryId, List<CommonDishDto> selectedDishes) {
        // 1. Validate that the category belongs to the eatery
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));

        if (!category.getEatery().getId().equals(eateryId)) {
            throw new NotYourResourceException("Category " + categoryId + " does not belong to eatery " + eateryId);
        }

        List<Long> createdDishIds = new ArrayList<>();

        for (CommonDishDto template : selectedDishes) {
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
                    .available(true)
                    .build();

            try {
                // Use the existing addDish service method
                DishEntity createdDish = addDish(dishDto, null);
                createdDishIds.add(createdDish.getId());

            } catch (Exception e) {
                log.error("Error creating dish from template: {}", template, e);
                // Re-throw to trigger a transactional rollback
                throw new RuntimeException("Failed to create dish from template: " + template.getNameEn(), e);

            }
        }
        return createdDishIds;
    }


}
