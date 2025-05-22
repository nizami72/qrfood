package az.qrfood.backend.category.repo;

import az.qrfood.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findById(Long id);
//    List<Category> findByRestaurantId(Long restaurantId);
}
