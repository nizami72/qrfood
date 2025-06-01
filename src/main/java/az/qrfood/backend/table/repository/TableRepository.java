package az.qrfood.backend.table.repository;

import az.qrfood.backend.table.entity.TableInEatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableInEatery, Long> {
    List<TableInEatery> findByEateryId(Long eateryId);
    Optional<TableInEatery> findByEateryIdAndTableNumber(Long eateryId, String tableNumber);
}