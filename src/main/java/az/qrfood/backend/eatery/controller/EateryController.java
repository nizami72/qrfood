package az.qrfood.backend.eatery.controller;

import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("${segment.api.eateries}")
public class EateryController {

    private final EateryService restaurantService;

    public EateryController(EateryService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * GET all eatery.

     * @return list of eatery
     */
    @GetMapping
    public ResponseEntity<List<EateryDto>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    /**
     * GET all eateries owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return list of eateries owned by the specified user
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<EateryDto>> getEateriesByOwnerId(@PathVariable("ownerId") Long ownerId) {
        log.debug("Request to get all eateries of owner [{}]", ownerId);
        return ResponseEntity.ok(restaurantService.getAllEateriesByOwnerId(ownerId));
    }


    /**
     * GET eatery by id.

     * @param id eatery ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EateryDto> getEateryById(@PathVariable("id") Long id) {
        log.debug("Request to get Eatery : {}", id);
        return ResponseEntity.ok(restaurantService.getEateryById(id));
    }

    /**
     * POST a new eatery.

     * @param eateryDto created eatery data
     * @return ID of created eatery
     */
    @PostMapping(consumes="application/json")
    public ResponseEntity<Long> createRestaurant(@RequestBody EateryDto eateryDto) {
        log.debug("Request to create eatery [{}]", eateryDto);
        return ResponseEntity.ok(restaurantService.createEatery(eateryDto));
    }

    /**
     * DELETE the eatery by ID.

     * @param id deleted eatery ID
     * @return todo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteEatery(@PathVariable("id") Long id) {
        return ResponseEntity.ok(restaurantService.deleteEatery(id));
    }

    /**
     * UPDATE an existing eatery.
     *
     * @param id The ID of the eatery to update
     * @param eateryDTO The updated eatery data
     * @return The ID of the updated eatery
     */
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<Long> updateEatery(@PathVariable("id") Long id, @RequestBody EateryDto eateryDTO) {
        log.debug("Request to update eatery with ID [{}]: {}", id, eateryDTO);
        return ResponseEntity.ok(restaurantService.updateEatery(id, eateryDTO));
    }
}
