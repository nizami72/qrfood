package az.qrfood.backend.kitchendepartment.controller;

import az.qrfood.backend.kitchendepartment.dto.CreateDepartmentRequestDto;
import az.qrfood.backend.kitchendepartment.dto.KitchenDepartmentDto;
import az.qrfood.backend.kitchendepartment.dto.UpdateDepartmentRequestDto;
import az.qrfood.backend.kitchendepartment.service.KitchenDepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// KitchenDepartmentController.java (Admin)
@RestController
@Validated
@Log4j2
public class KitchenDepartmentController {

    private final KitchenDepartmentService departmentService;

    public KitchenDepartmentController(KitchenDepartmentService departmentService) {
        this.departmentService = departmentService;
    }


    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${eatery.id.kitchen-department}")
    public List<KitchenDepartmentDto> getDepartmentsForRestaurant
            (@PathVariable(value = "eateryId", required = true) @NotNull Long eateryId) {
        log.debug("Get departments for eatery [{}]", eateryId);
        return departmentService.findByRestaurantId(eateryId);
    }

    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @PostMapping("${eatery.id.kitchen-department}")
    public KitchenDepartmentDto createDepartment(@PathVariable(value = "eateryId") Long eateryId,
                                                 @RequestBody CreateDepartmentRequestDto request) {

        log.debug("Create department [{}] for eatery [{}]", request.getName(), eateryId);
        assert eateryId.equals(request.getRestaurantId());
        return departmentService.create(request);
    }

    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @PutMapping("${eatery.id.kitchen-department.id}")
    public KitchenDepartmentDto updateDepartment(@PathVariable("eateryId") Long eateryId,
                                                 @PathVariable("departmentId") Long id,
                                                 @Valid @RequestBody UpdateDepartmentRequestDto request) {
        // todo ensure department id corresponds with eatery id
        return departmentService.update(id, request);
    }

    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @DeleteMapping("${eatery.id.kitchen-department.id}")
    public void deleteDepartment(@PathVariable("departmentId") Long id) {
        // todo ensure department id corresponds with eatery id
        departmentService.delete(id);
    }
}
