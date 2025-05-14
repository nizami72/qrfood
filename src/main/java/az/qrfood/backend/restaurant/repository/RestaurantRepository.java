package az.qrfood.backend.restaurant.repository;

import az.qrfood.backend.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
//    Optional<Restaurant> findByName(String name);
}
