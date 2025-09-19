package az.qrfood.backend.qr.repository;

import az.qrfood.backend.qr.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link QrCode} entity.
 * <p>
 * This interface provides standard CRUD (Create, Read, Update, Delete)
 * operations for {@link QrCode} entities. Spring Data JPA automatically
 * implements the methods of this interface at runtime.
 * </p>
 */
@Repository
public interface QrRepository extends JpaRepository<QrCode, Long> {

    @Query(value = """
        SELECT u.content 
        FROM qrfood.qr_code u
        JOIN table_in_eatery up ON up.id = u.id
        WHERE up.eatery_id = :eateryId
        """, nativeQuery = true)
    List<String> findAllByEateryId(@Param("eateryId") Long eateryId);

}
