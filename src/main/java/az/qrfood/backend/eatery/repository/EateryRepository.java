package az.qrfood.backend.eatery.repository;

import az.qrfood.backend.eatery.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {

    List<Eatery> findAllByOwnerId(Long ownerId);

//    Optional<Restaurant> findByName(String name);
}
