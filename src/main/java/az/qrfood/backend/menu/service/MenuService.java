package az.qrfood.backend.menu.service;

import az.qrfood.backend.menu.entity.MenuCategory;
import az.qrfood.backend.menu.entity.MenuItem;
import az.qrfood.backend.menu.repository.CategoryRepository;
import az.qrfood.backend.menu.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает все категории с блюдами для ресторана.
     */
    public Map<MenuCategory, List<MenuItem>> getMenuByRestaurant(Long restaurantId) {
//        List<MenuCategory> categories = categoryRepository.findByRestaurantId(restaurantId);
        Map<MenuCategory, List<MenuItem>> menu = new LinkedHashMap<>();
//        for (MenuCategory category : categories) {
//            List<MenuItem> items = menuItemRepository.findByCategoryId(category.getId())
//                    .stream().filter(MenuItem::getIsAvailable).toList();
//            menu.put(category, items);
//        }
        return menu;
    }
}
