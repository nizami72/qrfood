package az.qrfood.backend.category.repo;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.category.entity.CategoryStatus;
import az.qrfood.backend.dish.entity.DishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Category} entity.
 * <p>
 * This interface provides standard CRUD operations for {@link Category} entities
 * and supports custom query methods for retrieving categories by ID or hash.
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id The ID of the category to retrieve.
     * @return An {@link Optional} containing the found category, or empty if not found.
     */
    Optional<Category> findById(Long id);

    /**
     * Retrieves a category by its hash value.
     * <p>
     * The hash is typically used for quick lookups or to identify categories
     * based on their content (e.g., translations).
     * </p>
     *
     * @param hash The hash value of the category to retrieve.
     * @return An {@link Optional} containing the found category, or empty if not found.
     */
//    Optional<Category> findByHash(int hash);

    Optional<Category> findByEateryIdAndId(Long eateryId, Long id);

    @Query("""
           SELECT c
           FROM Category c
           LEFT JOIN FETCH c.items d
           WHERE c.eatery.id = :eateryId
           AND d.dishStatus = :status
           """)
    List<Category> findCategoryWithFilteredDishes(
            @Param("eateryId") Long eateryId,
            @Param("status") DishStatus status);

    @Query("""
           SELECT c
           FROM Category c
           LEFT JOIN FETCH c.items d
           WHERE c.eatery.id = :eateryId
           AND c.categoryStatus <> :excludedCategoryStatus
           AND d.dishStatus <> :excludedStatus
           """)
    List<Category> findCategoryWithDishesNotMatchingStatus(
            @Param ("excludedCategoryStatus") CategoryStatus status,
            @Param("eateryId") Long eateryId,
            @Param("excludedStatus") DishStatus excludedStatus);


    @Query("""
           SELECT c
           FROM Category c
           WHERE c.eatery.id = :eateryId
           AND c.categoryStatus <> :excludedCategoryStatus
           """)
    List<Category> findCategoryWithNotMatchingStatus(
            @Param ("excludedCategoryStatus") CategoryStatus status,
            @Param("eateryId") Long eateryId);


}
