package az.qrfood.backend.menu.service;

import az.qrfood.backend.menu.entity.CommonCategory;
import az.qrfood.backend.menu.entity.CommonCategoryTranslation;
import az.qrfood.backend.menu.repository.CommonCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CommonCategoryService {

    private final CommonCategoryRepository commonCategoryRepository;

    public CommonCategoryService(CommonCategoryRepository commonCategoryRepository) {
        this.commonCategoryRepository = commonCategoryRepository;
    }

    public List<CommonCategory> getAllCategories() {
        return commonCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public List<CommonCategory> getCategoriesWithTranslation(String locale) {
        return commonCategoryRepository.findAllWithTranslations(locale);
    }

    @Transactional
    public CommonCategory createCategory(String code, String defaultName) {
        CommonCategory category = new CommonCategory();
        category.setCode(code);
        
        CommonCategoryTranslation translation = new CommonCategoryTranslation();
        translation.setCategory(category);
        translation.setLocale("en"); // Default locale
        translation.setName(defaultName);
        
        category.getTranslations().add(translation);
        
        return commonCategoryRepository.save(category);
    }

    @Transactional
    public void addTranslation(Long categoryId, String locale, String name) {
        CommonCategory category = commonCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if translation already exists
        Optional<CommonCategoryTranslation> existingTranslation = category.getTranslations().stream()
                .filter(t -> t.getLocale().equals(locale))
                .findFirst();
        
        if (existingTranslation.isPresent()) {
            existingTranslation.get().setName(name);
        } else {
            CommonCategoryTranslation translation = new CommonCategoryTranslation();
            translation.setCategory(category);
            translation.setLocale(locale);
            translation.setName(name);
            category.getTranslations().add(translation);
        }
        
        commonCategoryRepository.save(category);
    }
}