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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EateryRepository eateryRepository;
    private final StorageService storageService;

    public CategoryService(CategoryRepository categoryRepository, EateryRepository eateryRepository, StorageService storageService) {
        this.categoryRepository = categoryRepository;
        this.eateryRepository = eateryRepository;
        this.storageService = storageService;
    }

    /**
     * Create new Category for particular eatery.

     * @param dishCategoryDto category data
     * @return Category
     */
    public Category createCategory(CategoryDto dishCategoryDto, MultipartFile multipartFile) {

        Long eateryId = dishCategoryDto.getEateryId();

        // check if eatery exists
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
        if(mayExistCategory.isPresent()) {
            Category c = mayExistCategory.get();
            log.debug("The category with the same eatery ID and translation already exists, id [{}]", c.getId());
            return c;
        }
        categoryRepository.save(category);
        log.debug("Dish category created [{}]", category);

        String folderPath = storageService.createCategoryFolder(category.getId());
        String fileName = category.getCategoryImageFileName();
        if(folderPath != null) {
            storageService.saveFile(folderPath, multipartFile, fileName);
            log.info("Dish file [{}] created at dir [{}]", fileName, folderPath);
        } else {
            log.error("Dish file [{}] was not created", fileName);
        }

        return category;// what to return id,id, dto or entity
    }



    /**
     * Todo implement all features
     * Returns the dish category ID from dishCategoryDto or finds the entity by values or creates new entity and
     * returns its ID.

     * @param dishCategoryDto dish category DTO
     * @return the id of Category entity
     */
    public Category createOrFindCategory(CategoryDto dishCategoryDto) {
        Long dishCategoryId = dishCategoryDto.getCategoryId();
        if (dishCategoryId != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(dishCategoryDto.getCategoryId());
            if(categoryOptional.isPresent()) {
                return categoryOptional.get();
            }
        }

        Long eateryId = dishCategoryDto.getEateryId();
        // todo try to find first then create if not found

        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "Cant create category for eatery %s, eatery not found", eateryId));
        }

        List<Category> categories = eateryOp.get().getCategories();
        if(categories.isEmpty()){
            log.warn("Eatery [{}] dish category is empty", eateryId);
        }

        Category category1 = Category.builder()
                .eatery(eateryOp.get())
                .build();

        Category category = categoryRepository.save(category1);
        long id = category.getId();


        log.debug("Dish category created [{}]", category1);
        return category1;
    }

    public List<CategoryDto> findAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return convertDishCategoryToDto(categories);
    }

    public CategoryDto findCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {throw new EntityNotFoundException(
                String.format("The category with id [%s] not fount", id));}
        return convertDishCategoryToDto(category.get());
    }

    public List<CategoryDto> findAllCategoryForEatery(long eateryId) {

        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {throw new EntityNotFoundException("Eatery not found"); }

        List<Category> categories = eateryOp.get().getCategories();
        if (categories.isEmpty()) {
            String error = String.format("Eatery [%s] has no any category and dish", eateryId);
            log.error(error);
            throw new EntityNotFoundException(error);
        }

        return convertDishCategoryToDto(categories);
    }

    private List<CategoryDto> convertDishCategoryToDto(List<Category> categories) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            categoryDtoList.add(convertDishCategoryToDto(category));
        }
        return categoryDtoList;
    }

    private CategoryDto convertDishCategoryToDto(Category category) {

            CategoryDto dto = new CategoryDto();
            dto.setEateryId(category.getEatery().getId());
            dto.setDishes(category.getItems().stream()
                    .map(DishService::convertEntityToDto)
                    .collect(Collectors.toList())
            );
            dto.setCategoryId(category.getId());

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

    public ResponseEntity<String> deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(String.format("Category [%s] deleted successfully", categoryId));
    }
}
