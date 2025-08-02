package az.qrfood.backend.user.service;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.dish.service.DishService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.user.UserUtils;
import az.qrfood.backend.user.dto.GeneralResponse;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.RegisterResponse;
import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.exception.UserAlreadyExistsException;
import az.qrfood.backend.user.repository.UserProfileRepository;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing {@link User} entities and their associated profiles.
 * <p>
 * This service handles user creation, retrieval, updating, and deletion,
 * as well as managing user roles and linking users to eateries.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;
    private final EateryService eateryService;
    private final CategoryService categoryService;
    private final DishService dishService;

    /**
     * Retrieves a list of all users in the system.
     *
     * @return A list of {@link UserResponse} representing all users.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all users that belong to a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link UserResponse} for users associated with the specified eatery.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Long eateryId) {
        List<UserProfile> profiles = userProfileRepository.findByRestaurantId(eateryId);
        return profiles.stream()
                .map(UserProfile::getUser)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A {@link UserResponse} representing the found user.
     * @throws EntityNotFoundException if the user with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserByUserId(id);
        return mapToResponse(user);
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A {@link UserResponse} representing the found user.
     * @throws EntityNotFoundException if the user with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long eateryId, Long id) {
        User user = findUserByEateryIdAndUserId(eateryId, id);
        return mapToResponse(user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return A {@link UserResponse} representing the found user.
     * @throws EntityNotFoundException if the user with the given username is not found.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = findUserByUsername(username);
        return mapToResponse(user);
    }


    @Transactional
    public UserResponse updateUser(Long eateryId, Long id, UserRequest request) {
        User user = findUserByEateryIdAndUserId(eateryId, id);
        return updateUserI(user, request);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserByUserId(id);
        return updateUserI(user, request);
    }

    /**
     * Updates an existing userUnderChange.
     * <p>
     * This method allows updating the username, password (if provided), roles, and userUnderChange profile name.
     * </p>
     *
     * @param userNewData The {@link UserRequest} containing the updated userUnderChange data.
     * @return A {@link UserResponse} representing the updated userUnderChange.
     * @throws EntityNotFoundException if the userUnderChange with the given ID is not found.
     * @throws IllegalStateException   if the new username already exists.
     */
    protected UserResponse updateUserI(User userUnderChange, UserRequest userNewData) {

        if (!userUnderChange.getUsername().equals(userNewData.getUsername())) {
            validateUserDoesNotExist(userNewData.getUsername());
            userUnderChange.setUsername(userNewData.getUsername());
        }

        // Only update password if it's provided
        if (userNewData.getPassword() != null && !userNewData.getPassword().isEmpty()) {
            userUnderChange.setPassword(passwordEncoder.encode(userNewData.getPassword()));
        }

        // Only update roles if they're provided
        if (userNewData.getRoles() != null) {

            int i = UserUtils.compareRoles(userUnderChange.getRoles());
            log.debug(" i [{}]", i);
            // if 1 decline
            if(i < 1) {
                userUnderChange.setRoles(userNewData.getRoles());
            } else {
                log.debug("The current role is elidible to change higher userUnderChange role [{}]", "todo userUnderChange");

            }

            if(!userUnderChange.getRoles().contains(Role.SUPER_ADMIN)) {
                userNewData.getRoles().remove(Role.SUPER_ADMIN);
            }
        }

        // Only update name if it provided
        if (userNewData.getName() != null) {
            UserProfile userProfile = userProfileRepository.findByUser(userUnderChange)
                    .orElseThrow(() -> new EntityNotFoundException("User profile not found for userUnderChange: " + userUnderChange.getUsername()));
            userProfile.setName(userNewData.getName());
            userProfileRepository.save(userProfile);
            log.debug("Updated name of the userUnderChange [{}]", userUnderChange.getUsername());
        }

        User updatedUser = userRepository.save(userUnderChange);
        return mapToResponse(updatedUser);
    }

    /**
     * Deletes a user by their ID along with all associated resources.
     * This includes:
     * - All eateries owned by the user
     * - All categories in those eateries
     * - All dishes in those categories
     * - The user profile
     *
     * @param id The ID of the user to delete.
     * @throws EntityNotFoundException if the user with the given ID is not found.
     */
    @Transactional
    public void deleteUser(Long id) {
        // Find the user
        User user = findUserByUserId(id);
        log.info("Deleting user with ID: {} and username: {}", id, user.getUsername());

        // Find the user profile
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUser(user);
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            log.info("Found user profile with ID: {}", userProfile.getId());

            // Get all eateries associated with the profile
            List<Long> eateryIds = userProfile.getRestaurantIds();
            log.info("User has {} eateries to delete", eateryIds.size());

            // For each eatery
            for (Long eateryId : eateryIds) {
                try {
                    // Find all categories for this eatery
                    List<CategoryDto> categories = categoryService.findAllCategoryForEatery(eateryId);
                    log.info("Eatery ID: {} has {} categories to delete", eateryId, categories.size());

                    // For each category, delete all dishes
                    for (CategoryDto category : categories) {
                        try {
                            // Delete all dishes in this category
                            log.info("Deleting dishes for category ID: {}", category.getCategoryId());
                            dishService.getAllDishesInCategory(category.getCategoryId()).forEach(dish -> {
                                try {
                                    dishService.deleteDishItemById(category.getCategoryId(), dish.getDishId());
                                    log.debug("Deleted dish ID: {} from category ID: {}", dish.getDishId(), category.getCategoryId());
                                } catch (Exception e) {
                                    log.error("Error deleting dish ID: {} from category ID: {}", dish.getDishId(), category.getCategoryId(), e);
                                }
                            });

                            // Delete the category
                            categoryService.deleteCategory(category.getCategoryId());
                            log.debug("Deleted category ID: {}", category.getCategoryId());
                        } catch (Exception e) {
                            log.error("Error deleting category ID: {}", category.getCategoryId(), e);
                        }
                    }

                    // Delete the eatery
                    eateryService.deleteEatery(eateryId);
                    log.info("Deleted eatery ID: {}", eateryId);
                } catch (Exception e) {
                    log.error("Error deleting eatery ID: {}", eateryId, e);
                }
            }

            // Delete the user profile
            userProfileRepository.delete(userProfile);
            log.info("Deleted user profile ID: {}", userProfile.getId());
        } else {
            log.warn("No user profile found for user ID: {}", id);
        }

        // Delete the user
        userRepository.delete(user);
        log.info("Deleted user ID: {}", id);
    }

    /**
     * Deletes a user by their username.
     *
     * @param username The username of the user to delete.
     * @throws EntityNotFoundException if the user with the given username is not found.
     */
    @Transactional
    public GeneralResponse deleteUser(String username) {
        if (userRepository.findByUsername(username).isEmpty()) {
            log.debug("User with user name [{}] not found", username);
            return GeneralResponse.builder()
                    .message("User not found with username: " + username)
                    .success(false)
                    .build();
        }
        Optional<User> opUser = userRepository.deleteUserByUsername(username);
        if(opUser.isPresent()) {
            log.debug("User with user name [{}] deleted", username);
            return GeneralResponse.builder()
                    .message("User deleted successfully")
                    .success(true)
                    .build();
        } else {
            log.debug("Optional with user name [{}] is empty", username);
            return GeneralResponse.builder()
                    .message("User not found with username: " + username)
                    .success(false)
                    .build();
        }
    }

    @Transactional
    public ResponseEntity<RegisterResponse> registerAdminAndEatery(RegisterRequest request) {
        return registerUser(request, null, true);
    }

    @Transactional
    public ResponseEntity<RegisterResponse> registerEateryStaff(RegisterRequest request, Long eateryId) {
        return registerUser(request, eateryId, false);
    }

    private ResponseEntity<RegisterResponse> registerUser(RegisterRequest request, Long eateryId, boolean isEateryAdmin) {
        validateUserDoesNotExist(request.getUser().getEmail());

        if(isEateryAdmin) {
            Set<Role> roles = new HashSet<>();
            roles.add(Role.EATERY_ADMIN);
            request.getUser().setRoles(roles);
        }

        User user = createUserEntity(request.getUser().getEmail(), request.getUser().getPassword(), request.getUser().getRoles());
        UserProfile userProfile = userProfileService.createUserProfile(user, request.getUserProfileRequest());

        if (isEateryAdmin && request.getRestaurant() != null) {
            eateryId = createAndLinkEatery(userProfile, request.getRestaurant());
        } else if (eateryId != null) {
            userProfileService.addRestaurantToProfile(userProfile, eateryId);
        }

        log.debug("User [{}] successfully created.", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(user.getId(), eateryId, userProfile.getName(), "User registered successfully", true));
    }

    private void validateUserDoesNotExist(String email) {
        if (userRepository.findByUsername(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists.");
        }
    }

    private User createUserEntity(String username, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles != null ? roles : new HashSet<>());
        return userRepository.save(user);
    }

    private Long createAndLinkEatery(UserProfile profile, RegisterRequest.RestaurantDto restaurantDto) {
        EateryDto eateryDto = new EateryDto();
        eateryDto.setName(restaurantDto.getName());
        eateryDto.setNumberOfTables(0);
        eateryDto.setOwnerProfileId(profile.getId());
        Long eateryId = eateryService.createEatery(eateryDto);
        userProfileService.addRestaurantToProfile(profile, eateryId);
        return eateryId;
    }

    private User findUserByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    private User findUserByEateryIdAndUserId(Long eateryId, Long userId) {
        return userRepository.findByEateryIdAndUserId(eateryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    private UserResponse mapToResponse(User user) {
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found for user: " + user.getUsername()));
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                userProfile.getName(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                user.getProfile() != null
        );
    }
}
