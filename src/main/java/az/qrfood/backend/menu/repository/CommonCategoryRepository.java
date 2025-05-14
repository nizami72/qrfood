package az.qrfood.backend.menu.repository;

import az.qrfood.backend.menu.entity.CommonCategory;
import az.qrfood.backend.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Locale;

public interface CommonCategoryRepository  extends JpaRepository<CommonCategory, Long> {

    List<CommonCategory> findAllByOrderByDisplayOrderAsc();

    List<CommonCategory> findAllWithTranslations(String locale);

}
