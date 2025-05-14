# Feature-Based Implementation Example

This document provides a detailed example of how the code would be organized in the proposed feature-based structure, focusing on the restaurant feature as an example.

## Restaurant Feature Package Structure

```
az.qrfood.backend.restaurant
├── controller
│   └── RestaurantController.java
├── dto
│   └── RestaurantDTO.java
├── entity
│   ├── Restaurant.java
│   └── RestaurantPhone.java
├── repository
│   └── RestaurantRepository.java
└── service
    └── RestaurantService.java
```

## Sample Implementation

### Entity Layer

**Restaurant.java**
```java
package az.qrfood.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant that uses the QR food ordering system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantPhone> phones = new ArrayList<>();

    // References to other features
    // These would be handled through proper JPA mappings
    // and the appropriate imports from other feature packages

    private Double geoLat;

    private Double geoLng;
}
```

**RestaurantPhone.java**
```java
package az.qrfood.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_phone")
public class RestaurantPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private String phoneNumber;

    private String description;
}
```

### Repository Layer

**RestaurantRepository.java**
```java
package az.qrfood.backend.restaurant.repository;

import az.qrfood.backend.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Restaurant-specific query methods
}
```

### DTO Layer

**RestaurantDTO.java**
```java
package az.qrfood.backend.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String address;
    private List<String> phoneNumbers;
    private Double geoLat;
    private Double geoLng;
}
```

### Service Layer

**RestaurantService.java**
```java
package az.qrfood.backend.restaurant.service;

import az.qrfood.backend.restaurant.dto.RestaurantDTO;
import az.qrfood.backend.restaurant.entity.Restaurant;
import az.qrfood.backend.restaurant.repository.RestaurantRepository;
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

    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        List<String> phoneNumbers = restaurant.getPhones().stream()
                .map(phone -> phone.getPhoneNumber())
                .collect(Collectors.toList());

        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                phoneNumbers,
                restaurant.getGeoLat(),
                restaurant.getGeoLng()
        );
    }
}
```

### Controller Layer

**RestaurantController.java**
```java
package az.qrfood.backend.restaurant.controller;

import az.qrfood.backend.restaurant.dto.RestaurantDTO;
import az.qrfood.backend.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }
}
```

## Cross-Feature Relationships

In a feature-based structure, relationships between entities in different features need to be handled carefully. Here are some approaches:

### 1. Direct References with Imports

When a feature needs to reference entities from another feature, it can import them directly:

```java
// In Restaurant.java
import az.qrfood.backend.menu.entity.MenuCategory;
import az.qrfood.backend.table.entity.TableInRestaurant;

@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
private List<TableInRestaurant> tables;

@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
private List<MenuCategory> categories;
```

### 2. Using Interfaces for Cross-Feature Communication

For service-level interactions, define interfaces in a common package:

```java
// In common package
package az.qrfood.backend.common.service;

public interface RestaurantInfoProvider {
    String getRestaurantName(Long restaurantId);
    boolean restaurantExists(Long restaurantId);
}

// In restaurant feature
package az.qrfood.backend.restaurant.service;

import az.qrfood.backend.common.service.RestaurantInfoProvider;

@Service
public class RestaurantService implements RestaurantInfoProvider {
    // Implementation
}
```

### 3. Using Events for Loose Coupling

For even looser coupling, use Spring's event system:

```java
// In restaurant feature
@Service
public class RestaurantService {
    private final ApplicationEventPublisher eventPublisher;

    public void updateRestaurant(Restaurant restaurant) {
        // Save restaurant
        eventPublisher.publishEvent(new RestaurantUpdatedEvent(restaurant.getId()));
    }
}

// In menu feature
@Service
public class MenuService {
    @EventListener
    public void handleRestaurantUpdated(RestaurantUpdatedEvent event) {
        // Update menus for the restaurant
    }
}
```

## Conclusion

This example demonstrates how the restaurant feature would be organized in the proposed feature-based structure. Each feature has its own set of controllers, services, repositories, DTOs, and entities, making it self-contained and easier to understand. Cross-feature relationships are handled through imports, interfaces, or events, depending on the level of coupling required.

The same pattern would be applied to the other features (menu, table, order), resulting in a codebase that is organized around business capabilities rather than technical layers.