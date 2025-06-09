package az.qrfood.backend.client.repository;


import az.qrfood.backend.client.entity.ClientDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientDeviceRepository extends JpaRepository<ClientDevice, Long> {

    Optional<ClientDevice> findByUuid(String uuid);

}
