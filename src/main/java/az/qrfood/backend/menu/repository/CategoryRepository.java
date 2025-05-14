package az.qrfood.backend.menu.repository;

import az.qrfood.backend.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<MenuCategory, Long> {
//    List<MenuCategory> findByRestaurantId(Long restaurantId);
}
