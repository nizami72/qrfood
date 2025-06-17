package az.qrfood.backend.useraccess.controller;

import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.useraccess.dto.UserAccessRequest;
import az.qrfood.backend.useraccess.dto.UserAccessResponse;
import az.qrfood.backend.useraccess.service.UserAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing UserAccess entities.
 */
@RestController
@RequestMapping("/api/user-access")
@RequiredArgsConstructor
public class UserAccessController {

    private final UserAccessService userAccessService;

    /**
     * POST a new user access record.
     *
     * @param request the user access request
     * @return the created user access response
     */
    @PostMapping
    public ResponseEntity<UserAccessResponse> createUserAccess(@Valid @RequestBody UserAccessRequest request) {
        UserAccessResponse response = userAccessService.createUserAccess(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET all user access records.
     *
     * @return list of user access responses
     */
    @GetMapping
    public ResponseEntity<List<UserAccessResponse>> getAllUserAccess() {
        List<UserAccessResponse> responses = userAccessService.getAllUserAccess();
        return ResponseEntity.ok(responses);
    }

    /**
     * GET a user access record by ID.
     *
     * @param id the user access ID
     * @return the user access response
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAccessResponse> getUserAccessById(@PathVariable Long id) {
        UserAccessResponse response = userAccessService.getUserAccessById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET all user access records for a specific user.
     *
     * @param userId the user ID
     * @return list of user access responses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAccessResponse>> getUserAccessByUserId(@PathVariable Long userId) {
        List<UserAccessResponse> responses = userAccessService.getUserAccessByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET all user access records for a specific eatery.
     *
     * @param eateryId the eatery ID
     * @return list of user access responses
     */
    @GetMapping("/eatery/{eateryId}")
    public ResponseEntity<List<UserAccessResponse>> getUserAccessByEateryId(@PathVariable Long eateryId) {
        List<UserAccessResponse> responses = userAccessService.getUserAccessByEateryId(eateryId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET all user access records with a specific role.
     *
     * @param role the role
     * @return list of user access responses
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserAccessResponse>> getUserAccessByRole(@PathVariable Role role) {
        List<UserAccessResponse> responses = userAccessService.getUserAccessByRole(role);
        return ResponseEntity.ok(responses);
    }

    /**
     * PUT a user access record.
     *
     * @param id the user access ID
     * @param request the user access request
     * @return the updated user access response
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAccessResponse> updateUserAccess(
            @PathVariable Long id,
            @Valid @RequestBody UserAccessRequest request) {
        UserAccessResponse response = userAccessService.updateUserAccess(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE a user access record.
     *
     * @param id the user access ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAccess(@PathVariable Long id) {
        userAccessService.deleteUserAccess(id);
        return ResponseEntity.noContent().build();
    }
}