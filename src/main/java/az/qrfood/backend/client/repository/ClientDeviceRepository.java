package az.qrfood.backend.client.repository;


import az.qrfood.backend.client.entity.ClientDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ClientDevice} entity.
 * <p>
 * This interface provides standard CRUD operations for {@link ClientDevice} entities
 * and supports custom query methods for retrieving client devices by their UUID.
 * </p>
 */
public interface ClientDeviceRepository extends JpaRepository<ClientDevice, Long> {

    /**
     * Retrieves a {@link ClientDevice} by its unique UUID.
     *
     * @param uuid The UUID of the client device to retrieve.
     * @return An {@link Optional} containing the found {@link ClientDevice}, or empty if not found.
     */
    Optional<ClientDevice> findByUuid(String uuid);

    List<ClientDevice> findByOrdersId(Long id);
}
