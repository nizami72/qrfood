package az.qrfood.backend.qr.repository;

import az.qrfood.backend.qr.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrRepository extends JpaRepository<QrCode, Long> {
}
