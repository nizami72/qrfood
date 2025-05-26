package az.qrfood.backend.client.service;

import az.qrfood.backend.category.entity.CategoryTranslation;
import az.qrfood.backend.client.dto.ClientCategoryItem;
import az.qrfood.backend.client.dto.ClientMenu;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.dish.entity.DishEntityTranslation;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Log4j2
@RestController
public class ClientService {

    private final EateryRepository eateryRepository;

    public ClientService(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
    }

    public ClientMenu getClientMenu(@PathVariable("eatery-id") Long eateryId) {
        Locale current = LocaleContextHolder.getLocale();
        Eatery eatery = eateryRepository.findById(eateryId).orElseThrow();

        List<az.qrfood.backend.client.dto.ClientCategory> clientCategories = new ArrayList<>();

        eatery.getCategories().forEach(category -> {
            // Find category translation for current locale
            Optional<String> categoryName = category.getTranslations().stream()
                    .filter(tr -> tr.getLang().equals(current.getLanguage()))
                    .map(CategoryTranslation::getName)
                    .findFirst();

            // If no translation found for current locale, use the first available
            String categoryNameStr = categoryName.orElseGet(() -> 
                category.getTranslations().isEmpty() ? "Unknown Category" : category.getTranslations().getFirst().getName());

            List<ClientCategoryItem> clientCategoryItems = new ArrayList<>();

            category.getItems().forEach(item -> {
                // Skip unavailable items
                if (!item.isAvailable()) {
                    return;
                }

                // Find menu item translation for current locale
                Optional<DishEntityTranslation> itemTranslation = item.getTranslations().stream()
                        .filter(tr -> tr.getLang().equals(current.getLanguage()))
                        .findFirst();

                // If no translation found for current locale, use the first available
                DishEntityTranslation translation = itemTranslation.orElseGet(() ->
                    item.getTranslations().isEmpty() ? null : item.getTranslations().getFirst());

                if (translation != null) {
                    ClientCategoryItem clientCategoryItem = new ClientCategoryItem(
                            translation.getName(),
                            translation.getDescription(),
                            item.getPrice(),
                            item.getImage()
                    );
                    clientCategoryItems.add(clientCategoryItem);
                }
            });

            // Only add a category if it has items
            if (!clientCategoryItems.isEmpty()) {
                az.qrfood.backend.client.dto.ClientCategory clientCategory = 
                    new az.qrfood.backend.client.dto.ClientCategory(categoryNameStr, clientCategoryItems);
                clientCategories.add(clientCategory);
            }
        });

        return new ClientMenu(clientCategories);
    }

}
