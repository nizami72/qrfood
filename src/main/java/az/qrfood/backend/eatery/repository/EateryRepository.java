package az.qrfood.backend.eatery.repository;

import az.qrfood.backend.eatery.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link Eatery} entity.
 * <p>
 * This interface provides the standard CRUD (Create, Read, Update, Delete)
 * operations for {@link Eatery} entities, as well as the ability to define
 * custom query methods. Spring Data JPA automatically implements the methods
 * of this interface at runtime.
 * </p>
 */
@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {

}
