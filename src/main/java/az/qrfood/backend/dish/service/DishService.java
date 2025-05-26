package az.qrfood.backend.dish.service;

import az.qrfood.backend.category.dto.DishCategoryDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;

    public DishService(DishRepository dishRepository,
                       CategoryRepository categoryRepository, StorageService storageService) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }

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

        storageService.saveFile(folder, multipartFile, dishEntity.getImage());

        return dishEntity;
    }

    private DishCategoryDto convertDtoToEntity(DishDto dish) {
        return DishCategoryDto.builder()
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
}
