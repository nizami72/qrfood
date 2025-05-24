package az.qrfood.backend.category.service;

import az.qrfood.backend.category.dto.MenuCategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.entity.CategoryTranslation;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.lang.Language;
import az.qrfood.backend.menu.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EateryRepository eateryRepository;

    public CategoryService(CategoryRepository categoryRepository, EateryRepository eateryRepository) {
        this.categoryRepository = categoryRepository;
        this.eateryRepository = eateryRepository;
    }

    /**
     * Create new Category for particular eatery.

     * @param menuCategoryDto category data
     * @return Category
     */
    public Category createCategory(MenuCategoryDto menuCategoryDto) {

        Long eateryId = menuCategoryDto.getEateryId();

        // check if eatery exists
        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "Cant create category for eatery %s, eatery not found", eateryId));
        }

        Category category = Category.builder()
                .eatery(eateryOp.get())
                .iconUrl(null)
                .build();

        List<CategoryTranslation> categoryTranslations = List.of(
                new CategoryTranslation(category, Language.az.name(), menuCategoryDto.getNameAz()),
                new CategoryTranslation(category, Language.en.name(), menuCategoryDto.getNameEn()),
                new CategoryTranslation(category, Language.ru.name(), menuCategoryDto.getNameRu())
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
        log.debug("Menu category created [{}]", category);
        return category;
    }



    /**
     * Todo implement all features
     * Returns the menu category ID from menuCategoryDto or finds the entity by values or creates new entity and
     * returns its ID.

     * @param menuCategoryDto menu category DTO
     * @return the id of Category entity
     */
    public Category createOrFindCategory(MenuCategoryDto menuCategoryDto) {
        Long menuCategoryId = menuCategoryDto.getCategoryId();
        if (menuCategoryId != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(menuCategoryDto.getCategoryId());
            if(categoryOptional.isPresent()) {
                return categoryOptional.get();
            }
        }

        Long eateryId = menuCategoryDto.getEateryId();
        // todo try to find first then create if not found

        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "Cant create category for eatery %s, eatery not found", eateryId));
        }

        List<Category> menuCategories = eateryOp.get().getCategories();
        if(menuCategories.isEmpty()){
            log.warn("Eatery [{}] menu category is empty", eateryId);
        }

        Category menuCategory = Category.builder()
                .eatery(eateryOp.get())
                .iconUrl(null)
                .build();

        Category category = categoryRepository.save(menuCategory);
        long id = category.getId();


        log.debug("Menu category created [{}]", menuCategory);
        return menuCategory;
    }

    public List<MenuCategoryDto> findAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return convertMenuCategoryToDto(categories);
    }

    public MenuCategoryDto findCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {throw new EntityNotFoundException(
                String.format("The category with id [%s] not fount", id));}
        return convertMenuCategoryToDto(category.get());
    }

    public List<MenuCategoryDto> findAllCategoryForEatery(long eateryId) {

        Optional<Eatery> eateryOp = eateryRepository.findById(eateryId);
        if (eateryOp.isEmpty()) {throw new EntityNotFoundException("Eatery not found"); }

        List<Category> categories = eateryOp.get().getCategories();
        if (categories.isEmpty()) {
            String error = String.format("Eatery [%s] has no any category and menu", eateryId);
            log.error(error);
            throw new EntityNotFoundException(error);
        }

        return convertMenuCategoryToDto(categories);
    }

    private List<MenuCategoryDto> convertMenuCategoryToDto(List<Category> categories) {
        List<MenuCategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            categoryDtoList.add(convertMenuCategoryToDto(category));
        }
        return categoryDtoList;
    }

    private MenuCategoryDto convertMenuCategoryToDto(Category category) {

            MenuCategoryDto dto = new MenuCategoryDto();
            dto.setEateryId(category.getEatery().getId());
            dto.setDishes(category.getItems().stream()
                    .map(MenuService::convertEntityToDto)
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
