package az.qrfood.backend.menu.service;

import az.qrfood.backend.category.dto.MenuCategoryDto;
import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.repo.CategoryRepository;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.lang.Language;
import az.qrfood.backend.menu.dto.MenuItemDto;
import az.qrfood.backend.menu.entity.MenuItem;
import az.qrfood.backend.menu.entity.MenuItemTranslation;
import az.qrfood.backend.menu.repository.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuItemRepository menuItemRepository,
                       CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns all MenuItems for particular category.

     * @param categoryId the category ID
     */
    public List<MenuItemDto> getAllMenusInCategory(Long categoryId) {
        Optional<Category> categories = categoryRepository.findById(categoryId);
        if(categories.isEmpty()) {
            throw new EntityNotFoundException("Category with id " + categoryId + " not found");
        }

        List<MenuItem> menuItems = categories.get().getItems();
        List<MenuItemDto> menuItemDtos = new ArrayList<>();

        menuItems.forEach(menuItem -> {
            menuItemDtos.add(convertEntityToDto(menuItem));

        });
        return menuItemDtos;
    }


    public MenuItem addMenuItem(MenuItemDto dto) {
        Optional<Category> optionalCategory = categoryRepository.findById(dto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }

        MenuItem menuItem = Util.copyProperties(dto, MenuItem.class);
        menuItem.setTranslations(new ArrayList<>());
        menuItem.setCategory(optionalCategory.get());

        menuItem = menuItemRepository.save(menuItem);

        List<MenuItemTranslation> translations = List.of(
                new MenuItemTranslation(menuItem, Language.az.name(), dto.getNameAz(), dto.getDescriptionAz()),
                new MenuItemTranslation(menuItem, Language.en.name(), dto.getNameEn(), dto.getDescriptionEn()),
                new MenuItemTranslation(menuItem, Language.ru.name(), dto.getNameRu(), dto.getDescriptionRu())
        );
        log.debug("Menu Item created [{}]", menuItem);
        menuItem.getTranslations().addAll(translations);
        menuItemRepository.save(menuItem);
        return menuItem;
    }

    private MenuCategoryDto convertDtoToEntity(MenuItemDto menuItemDto) {
        return MenuCategoryDto.builder()
                .eateryId(menuItemDto.getCategoryId())
                .nameEn(menuItemDto.getNameEn())
                .nameRu(menuItemDto.getNameRu())
                .nameAz(menuItemDto.getNameAz())
                .build();
    }

    public static MenuItemDto convertEntityToDto(MenuItem menuItem) {
        MenuItemDto dto = MenuItemDto.builder()
                .dishId(menuItem.getId())
                .categoryId(menuItem.getCategory().getId())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .isAvailable(menuItem.isAvailable())
                .build();

        menuItem.getTranslations().forEach(t -> {
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
