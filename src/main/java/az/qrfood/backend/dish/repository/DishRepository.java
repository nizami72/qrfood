package az.qrfood.backend.dish.repository;

import az.qrfood.backend.dish.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link DishEntity} entity.
 * <p>
 * This interface provides standard CRUD (Create, Read, Update, Delete)
 * operations for {@link DishEntity} entities. Spring Data JPA automatically
 * implements the methods of this interface at runtime.
 * </p>
 */
@Repository
public interface DishRepository extends JpaRepository<DishEntity, Long> {
}
