package az.qrfood.backend.eatery.controller;

import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/eatery")
public class EateryController {

    private final EateryService restaurantService;

    public EateryController(EateryService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Get all eatery.

     * @return list of eatery
     */
    @GetMapping
    public ResponseEntity<List<EateryDto>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    /**
     * Get eatery by id.

     * @param id eatery ID
     */
    @GetMapping("/{eatery-id}")
    public ResponseEntity<EateryDto> getEateryById(@PathVariable("eatery-id") Long id) {
        return ResponseEntity.ok(restaurantService.getEateryById(id));
    }

    /**
     * Create a new eatery.

     * @param restaurantDTO cretaed eatery data
     * @return
     */
    @PostMapping(consumes="application/json")
    public ResponseEntity<Long> createRestaurant(@RequestBody EateryDto restaurantDTO) {
        log.debug("Request to create eatery [{}]", restaurantDTO);
        return ResponseEntity.ok(restaurantService.createEatery(restaurantDTO));
    }

    /**
     * Delete the eatery by ID.

     * @param id deleted eatery ID
     * @return todo
     */
    @DeleteMapping("/{eatery-id}")
    public ResponseEntity<Long> deleteEatery(@PathVariable("eatery-id") Long id) {
        return ResponseEntity.ok(restaurantService.deleteEatery(id));

    }
}