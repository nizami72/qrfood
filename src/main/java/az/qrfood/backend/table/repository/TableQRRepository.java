package az.qrfood.backend.table.repository;

import az.qrfood.backend.table.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableQRRepository extends JpaRepository<QrCode, Long> {
}
