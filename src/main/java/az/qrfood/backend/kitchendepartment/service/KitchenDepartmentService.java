package az.qrfood.backend.kitchendepartment.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.kitchendepartment.dto.CreateDepartmentRequestDto;
import az.qrfood.backend.kitchendepartment.dto.KitchenDepartmentDto;
import az.qrfood.backend.kitchendepartment.dto.UpdateDepartmentRequestDto;
import az.qrfood.backend.kitchendepartment.entity.KitchenDepartmentEntity;
import az.qrfood.backend.kitchendepartment.repository.KitchenDepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling business logic related to Kitchen Departments.
 */
@Service
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class KitchenDepartmentService {

    private final KitchenDepartmentRepository departmentRepository;
    private final EateryRepository restaurantRepository; // Assuming you have this repository
    private final az.qrfood.backend.user.service.UserProfileService userProfileService;

    private void assertUserHasAccessToRestaurant(Long restaurantId) {
        var profileOpt = userProfileService.findCurrentUserProfile();
        if (profileOpt.isEmpty()) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        boolean hasAccess = profileOpt.get().getEateries()
                .stream()
                .anyMatch(e -> e.getId().equals(restaurantId));
        if (!hasAccess) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have access to this restaurant");
        }
    }

    /**
     * Creates a new kitchen department for a given restaurant.
     *
     * @param request The request object containing the name and restaurant ID.
     * @return A DTO of the newly created department.
     * @throws EntityNotFoundException if the restaurant with the given ID does not exist.
     */
    @Transactional
    public KitchenDepartmentDto create(CreateDepartmentRequestDto request) {
        assertUserHasAccessToRestaurant(request.getRestaurantId());
        Eatery restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

        KitchenDepartmentEntity newDepartment = new KitchenDepartmentEntity();
        newDepartment.setName(request.getName());
        newDepartment.setRestaurant(restaurant);

        KitchenDepartmentEntity savedDepartment = departmentRepository.save(newDepartment);

        return toDto(savedDepartment);
    }

    /**
     * Updates an existing kitchen department's name.
     */
    @Transactional
    public KitchenDepartmentDto update(Long departmentId, UpdateDepartmentRequestDto request) {
        KitchenDepartmentEntity dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + departmentId));
        assertUserHasAccessToRestaurant(dept.getRestaurant().getId());
        dept.setName(request.getName());
        KitchenDepartmentEntity saved = departmentRepository.save(dept);
        return toDto(saved);
    }

    /**
     * Deletes a kitchen department by id (requires access to the owning restaurant).
     */
    @Transactional
    public void delete(Long eateryId, Long departmentId) {
        KitchenDepartmentEntity dept = departmentRepository.findByIdAndRestaurantId(eateryId, departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + departmentId));
        assertUserHasAccessToRestaurant(dept.getRestaurant().getId());
        departmentRepository.delete(dept);
    }

    /**
     * Finds all kitchen departments for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of department DTOs.
     */
    @Transactional(readOnly = true)
    public List<KitchenDepartmentDto> findByRestaurantId(Long restaurantId) {
        assertUserHasAccessToRestaurant(restaurantId);
        return departmentRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert a KitchenDepartmentEntity entity to a DepartmentDto.
     *
     * @param department The entity to convert.
     * @return The resulting DTO.
     */
    private KitchenDepartmentDto toDto(KitchenDepartmentEntity department) {
        return new KitchenDepartmentDto(department.getId(), department.getName());
    }
}
