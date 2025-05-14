package az.qrfood.backend.menu.controller;

import az.qrfood.backend.menu.dto.CommonCategoryDTO;
import az.qrfood.backend.menu.entity.CommonCategory;
import az.qrfood.backend.menu.service.CommonCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/common-categories")
public class CommonCategoryController {
    
    private final CommonCategoryService categoryService;
    private final LocaleResolver localeResolver;

    public CommonCategoryController(CommonCategoryService categoryService, LocaleResolver localeResolver) {
        this.categoryService = categoryService;
        this.localeResolver = localeResolver;
    }

    @GetMapping
    public ResponseEntity<List<CommonCategoryDTO>> getCategories(HttpServletRequest request) {
        String locale = localeResolver.resolveLocale(request).getLanguage();
        List<CommonCategory> categories = categoryService.getCategoriesWithTranslation(locale);
        
        // Convert to DTOs with appropriate translations
        List<CommonCategoryDTO> dtos = categories.stream()
            .map(category -> new CommonCategoryDTO(
                category.getId(),
                category.getCode(),
                category.getNameForLocale(locale),
                category.getIconUrl()
            ))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }
    
    // Other endpoints for CRUD operations
}