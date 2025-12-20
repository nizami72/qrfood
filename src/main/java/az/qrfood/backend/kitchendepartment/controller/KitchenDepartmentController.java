package az.qrfood.backend.kitchendepartment.controller;

import az.qrfood.backend.constant.ApiRoutes;
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


    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping(ApiRoutes.KITCHEN_DEPT)
    public List<KitchenDepartmentDto> getDepartmentsForRestaurant(@PathVariable(value = "eateryId") @NotNull Long eateryId) {
        log.debug("Get departments for eatery [{}]", eateryId);
        return departmentService.findByRestaurantId(eateryId);
    }

    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @PostMapping(ApiRoutes.KITCHEN_DEPT)
    public KitchenDepartmentDto createDepartment(@PathVariable(value = "eateryId") Long eateryId,
                                                 @RequestBody CreateDepartmentRequestDto request) {

        log.debug("Create department [{}] for eatery [{}]", request.getName(), eateryId);
        assert eateryId.equals(request.getRestaurantId());
        return departmentService.create(request);
    }

    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @PutMapping(ApiRoutes.KITCHEN_DEPT_ID)
    public KitchenDepartmentDto updateDepartment(@PathVariable("eateryId") Long eateryId,
                                                 @PathVariable("departmentId") Long id,
                                                 @Valid @RequestBody UpdateDepartmentRequestDto request) {
        // todo ensure department id corresponds with eatery id
        return departmentService.update(id, request);
    }

    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN', 'KITCHEN_ADMIN')")
    @DeleteMapping(ApiRoutes.KITCHEN_DEPT_ID)
    public void deleteDepartment(@PathVariable("eateryId") Long eateryId,
                                 @PathVariable("departmentId") Long id) {
        departmentService.delete(id, eateryId);
    }
}
