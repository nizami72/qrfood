package az.qrfood.backend.qr.repository;

import az.qrfood.backend.qr.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
