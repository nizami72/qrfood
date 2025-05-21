package az.qrfood.backend.menu.controller;

import az.qrfood.backend.menu.dto.MenuItemDto;
import az.qrfood.backend.menu.entity.MenuItem;
import az.qrfood.backend.menu.repository.MenuItemRepository;
import az.qrfood.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;
    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuService menuService, MenuItemRepository menuItemRepository) {
        this.menuService = menuService;
        this.menuItemRepository = menuItemRepository;
    }

   /**
     * Retrieve the category menu by its ID.
     */
    @Operation(summary = "Retrieve the restourant menu by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant was not found")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<List<MenuItemDto>> getMenu(@PathVariable Long categoryId) {
        log.debug("Retrieve the category menu by ID");

        List<MenuItemDto> menuByCategory = menuService.getAllMenusInCategory(categoryId);
        return ResponseEntity.ok(menuByCategory);
    }

    /**
     * Create menu item for category.

     * @param menuItemDto - menu item DTO
     * @return Response entity
     */
    @PostMapping(value = "/create/for_category/{categoryId}", consumes = "application/json")
    public ResponseEntity<Long> createMenuItem(@PathVariable Long categoryId, @RequestBody MenuItemDto menuItemDto) {
        menuItemDto.setMenuCategoryId(categoryId);
        log.debug("Request to create menu item: {}", menuItemDto);
        MenuItem menuItem = menuService.addMenuItem(menuItemDto);
        return ResponseEntity.ok(menuItem.getId());
    }

    /**
     * Get menu Item by ID.

     * @param menuId the menu item ID
     * @return menu item dto
     */
    @GetMapping("/by_id/{menuId}")
    public MenuItemDto getMenuItemById(@PathVariable Long menuId) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(menuId);
        if(menuItem.isEmpty()) {
            throw new EntityNotFoundException("Menu item with id " + menuId + " not found");
        }
        return MenuService.convertEntityToDto(menuItem.get());
    }


    @DeleteMapping("/by_id/{menuId}")
    public ResponseEntity<String> deleteMenuItemById(@PathVariable Long menuId) {
        log.debug("Request to delete menu item with id {}", menuId);
        menuItemRepository.deleteById(menuId);
        return  ResponseEntity.ok("Menu item with id " + menuId + " deleted successfully");
    }

}