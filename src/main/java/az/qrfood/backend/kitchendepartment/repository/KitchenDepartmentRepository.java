package az.qrfood.backend.kitchendepartment.repository;

import az.qrfood.backend.kitchendepartment.entity.KitchenDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KitchenDepartmentRepository extends JpaRepository<KitchenDepartmentEntity, Long> {

    /**
     * Finds all kitchen departments associated with a specific restaurant.
     * @param restaurantId The ID of the restaurant.
     * @return A list of kitchen departments.
     */
    List<KitchenDepartmentEntity> findByRestaurantId(Long restaurantId);


}
