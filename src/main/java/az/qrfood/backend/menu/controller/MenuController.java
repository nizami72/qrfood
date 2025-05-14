package az.qrfood.backend.menu.controller;

import az.qrfood.backend.menu.entity.MenuCategory;
import az.qrfood.backend.menu.entity.MenuItem;
import az.qrfood.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

   /**
     * Получить меню ресторана по restaurantId.
     */
    @Operation(summary = "Retrieve the restourant menu by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant was not found")
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<Map<String, List<MenuItem>>> getMenu(@PathVariable Long restaurantId) {
        Map<MenuCategory, List<MenuItem>> menu = menuService.getMenuByRestaurant(restaurantId);
        
        // Преобразуем MenuCategory в строку, чтобы избежать проблем сериализации
        Map<String, List<MenuItem>> response = new LinkedHashMap<>();
        for (Map.Entry<MenuCategory, List<MenuItem>> entry : menu.entrySet()) {
            response.put(entry.getKey().getName(), entry.getValue());
        }
        return ResponseEntity.ok(response);
    }
}
