package az.qrfood.backend.restaurant.service;

import az.qrfood.backend.common.Util;
import az.qrfood.backend.restaurant.dto.RestaurantDTO;
import az.qrfood.backend.restaurant.entity.Restaurant;
import az.qrfood.backend.restaurant.entity.RestaurantPhone;
import az.qrfood.backend.restaurant.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RestaurantDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return convertToDTO(restaurant);
    }


    public ResponseEntity<RestaurantDTO> createRestaurant(RestaurantDTO restaurantDTO) {

        Restaurant r = Util.copyProperties(restaurantDTO, Restaurant.class);
        convertToEntity(restaurantDTO, r);
        restaurantRepository.save(r);
        return new ResponseEntity<>(convertToDTO(r), HttpStatus.CREATED);
    }

    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        List<String> phoneNumbers = restaurant.getPhones().stream()
                .map(RestaurantPhone::getPhoneNumber)
                .collect(Collectors.toList());
        RestaurantDTO dto = Util.copyProperties(restaurant, RestaurantDTO.class);
        dto.setPhones(phoneNumbers);
        return dto;
    }

    private Restaurant convertToEntity(RestaurantDTO restaurantDTO, Restaurant restaurant) {
        restaurant.setName(restaurantDTO.getName());
        restaurant.setAddress(restaurantDTO.getAddress());

        List<String> phoneNumbers = restaurant.getPhones().stream()
                .map(RestaurantPhone::getPhoneNumber)
                .toList();
        restaurant.setPhones(phoneNumbers);

        return restaurant;
    }
}