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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class DishService {

    //<editor-fold desc="Fields">
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public DishService(DishRepository dishRepository,
                       CategoryRepository categoryRepository, StorageService storageService) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }
    //</editor-fold>

    /**
     * Returns all DishItems for particular category.

     * @param categoryId the category ID
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
        dishRepository.save(dishEntity);

        Long categoryId = dishEntity.getCategory().getId();
        Long eateryId = dishEntity.getTranslations().get(0).getId();
        Long dishId = dishEntity.getId();

        String f = eateryId + System.lineSeparator() + categoryId  + System.lineSeparator() + dishId;

        String folder = storageService.createDishesFolder(dishEntity.getId());

        if(multipartFile != null) {
            storageService.saveFile(folder, multipartFile, dishEntity.getImage());
        }

        return dishEntity;
    }

    private CategoryDto convertDtoToEntity(DishDto dish) {
        return CategoryDto.builder()
                .eateryId(dish.getCategoryId())
                .nameEn(dish.getNameEn())
                .nameRu(dish.getNameRu())
                .nameAz(dish.getNameAz())
                .build();
    }

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

        // 🟢 Remove from list — this triggers orphanRemoval
        category.getItems().remove(dish);

        // No need to call dishRepository.deleteById()
        // Since orphanRemoval = true, it will be deleted automatically when the transaction commits.

        return ResponseEntity.ok(String.format("Dish [%s] deleted successfully", dishId));
    }

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

        // Update image if provided
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String folder = storageService.createDishesFolder(dishEntity.getId());
            storageService.saveFile(folder, multipartFile, dishEntity.getImage());
        }

        // Save the updated dish
        return dishRepository.save(dishEntity);
    }

}
