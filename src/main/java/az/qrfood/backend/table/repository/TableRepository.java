package az.qrfood.backend.table.repository;

import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.entity.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link TableInEatery} entity.
 * <p>
 * This interface provides standard CRUD operations for {@link TableInEatery} entities
 * and supports custom query methods for retrieving tables based on eatery ID and table number.
 * </p>
 */
@Repository
public interface TableRepository extends JpaRepository<TableInEatery, Long> {
    /**
     * Retrieves a list of tables belonging to a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link TableInEatery} entities associated with the given eatery ID.
     */
    List<TableInEatery> findByEateryId(Long eateryId);

    /**
     * Retrieves a table by its eatery ID and table number.
     *
     * @param eateryId    The ID of the eatery.
     * @param tableNumber The unique number or identifier of the table within the eatery.
     * @return An {@link Optional} containing the found {@link TableInEatery}, or empty if not found.
     */
    Optional<TableInEatery> findByEateryIdAndTableNumber(Long eateryId, String tableNumber);


    /**
     * Retrieves a list of not soft deleted tables belonging to a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link TableInEatery} entities associated with the given eatery ID.
     */

    @Query("""
            SELECT c
            FROM TableInEatery c
            WHERE c.eatery.id = :eateryId
            AND c.status <> :excludedCategoryStatus
            """)
    List<TableInEatery> findByEateryIdAndStausNot(Long eateryId, TableStatus excludedCategoryStatus);

}
