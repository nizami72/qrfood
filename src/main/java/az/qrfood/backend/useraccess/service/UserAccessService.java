package az.qrfood.backend.useraccess.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.useraccess.dto.UserAccessRequest;
import az.qrfood.backend.useraccess.dto.UserAccessResponse;
import az.qrfood.backend.useraccess.entity.UserAccess;
import az.qrfood.backend.useraccess.repository.UserAccessRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing UserAccess entities.
 */
@Service
@RequiredArgsConstructor
public class UserAccessService {

    private final UserAccessRepository userAccessRepository;
    private final UserRepository userRepository;
    private final EateryRepository eateryRepository;

    /**
     * Create a new user access record.
     *
     * @param request the user access request
     * @return the created user access response
     */
    @Transactional
    public UserAccessResponse createUserAccess(UserAccessRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Eatery eatery = eateryRepository.findById(request.getEateryId())
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found with id: " + request.getEateryId()));

        // Check if user access already exists
        userAccessRepository.findByUserAndEateryAndRole(user, eatery, request.getRole())
                .ifPresent(existingAccess -> {
                    throw new IllegalStateException("User access already exists for this user, eatery, and role");
                });

        UserAccess userAccess = new UserAccess();
        userAccess.setUser(user);
        userAccess.setEatery(eatery);
        userAccess.setRole(request.getRole());

        UserAccess savedUserAccess = userAccessRepository.save(userAccess);
        return mapToResponse(savedUserAccess);
    }

    /**
     * Get all user access records.
     *
     * @return list of user access responses
     */
    @Transactional(readOnly = true)
    public List<UserAccessResponse> getAllUserAccess() {
        return userAccessRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a user access record by ID.
     *
     * @param id the user access ID
     * @return the user access response
     */
    @Transactional(readOnly = true)
    public UserAccessResponse getUserAccessById(Long id) {
        UserAccess userAccess = userAccessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User access not found with id: " + id));
        return mapToResponse(userAccess);
    }

    /**
     * Get all user access records for a specific user.
     *
     * @param userId the user ID
     * @return list of user access responses
     */
    @Transactional(readOnly = true)
    public List<UserAccessResponse> getUserAccessByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return userAccessRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all user access records for a specific eatery.
     *
     * @param eateryId the eatery ID
     * @return list of user access responses
     */
    @Transactional(readOnly = true)
    public List<UserAccessResponse> getUserAccessByEateryId(Long eateryId) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found with id: " + eateryId));
        return userAccessRepository.findByEatery(eatery).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all user access records with a specific role.
     *
     * @param role the role
     * @return list of user access responses
     */
    @Transactional(readOnly = true)
    public List<UserAccessResponse> getUserAccessByRole(Role role) {
        return userAccessRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update a user access record.
     *
     * @param id the user access ID
     * @param request the user access request
     * @return the updated user access response
     */
    @Transactional
    public UserAccessResponse updateUserAccess(Long id, UserAccessRequest request) {
        UserAccess userAccess = userAccessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User access not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Eatery eatery = eateryRepository.findById(request.getEateryId())
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found with id: " + request.getEateryId()));

        // Check if another user access already exists with the same user, eatery, and role
        userAccessRepository.findByUserAndEateryAndRole(user, eatery, request.getRole())
                .ifPresent(existingAccess -> {
                    if (!existingAccess.getId().equals(id)) {
                        throw new IllegalStateException("Another user access already exists for this user, eatery, and role");
                    }
                });

        userAccess.setUser(user);
        userAccess.setEatery(eatery);
        userAccess.setRole(request.getRole());

        UserAccess updatedUserAccess = userAccessRepository.save(userAccess);
        return mapToResponse(updatedUserAccess);
    }

    /**
     * Delete a user access record.
     *
     * @param id the user access ID
     */
    @Transactional
    public void deleteUserAccess(Long id) {
        if (!userAccessRepository.existsById(id)) {
            throw new EntityNotFoundException("User access not found with id: " + id);
        }
        userAccessRepository.deleteById(id);
    }

    /**
     * Map a UserAccess entity to a UserAccessResponse DTO.
     *
     * @param userAccess the user access entity
     * @return the user access response DTO
     */
    private UserAccessResponse mapToResponse(UserAccess userAccess) {
        return new UserAccessResponse(
                userAccess.getId(),
                userAccess.getUser().getId(),
                userAccess.getUser().getUsername(),
                userAccess.getEatery().getId(),
                userAccess.getEatery().getName(),
                userAccess.getRole()
        );
    }
}