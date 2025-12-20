package az.qrfood.backend.user.service;

import az.qrfood.backend.auth.repository.AuthTokenRepository;
import az.qrfood.backend.auth.service.RefreshTokenService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.service.EateryLifecycleService;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.mail.dto.UserRegisteredEvent;
import az.qrfood.backend.tableassignment.entity.TableAssignment;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    //<editor-fold desc="Fields">
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;
    private final EateryService eateryService;
    private final az.qrfood.backend.tableassignment.repository.TableAssignmentRepository tableAssignmentRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthTokenRepository authTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EateryLifecycleService eateryLifecycleService;
    //</editor-fold>

    /**
     * Retrieves a list of all users in the system.
     *
     * @return A list of {@link UserResponse} representing all users.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
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
        return profiles.stream().map(UserProfile::getUser).map(this::mapToResponse).collect(Collectors.toList());
    }


    /**
     * Retrieves a list of all users that belong to a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link User} associated with the specified eatery.
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers(Long eateryId) {
        List<UserProfile> profiles = userProfileRepository.findByRestaurantId(eateryId);
        return profiles.stream().map(UserProfile::getUser).collect(Collectors.toList());
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
    public UserResponse updateUser(Long id, UserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        boolean currentIsEateryAdmin = false;
        long currentUserId = -1;
        if (principal instanceof User) {
            currentIsEateryAdmin = (((User) principal).getRoles().contains(Role.EATERY_ADMIN));
            currentUserId = ((User) principal).getId();
        }

        Set<Role> roles = request.getRoles();
        if (roles != null && !roles.isEmpty()) {
            request.getRoles().remove(Role.SUPER_ADMIN);
            if (roles.contains(Role.EATERY_ADMIN) && roles.size() > 1) {
                // eatery admin has all privileges so excluding all others
                roles.removeIf(role -> !role.equals(Role.EATERY_ADMIN));
            }
        }
        User user = findUserByUserId(id);
        // ensure EATERY_ADMIN cant decrease its own role
        if (currentIsEateryAdmin && id == currentUserId) {
            assert roles != null;
            roles.clear();
            roles.add(Role.EATERY_ADMIN);
            log.warn("Eatery admin tried to decrease its own role [{}]", user.getUsername());
        }
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
    protected UserResponse updateUserI(User userUnderChange, @Valid UserRequest userNewData) {

        String mayBeUserName = userNewData.getUsername();
        if (StringUtils.hasLength(mayBeUserName) && !userUnderChange.getUsername().equals(mayBeUserName)) {
            validateUserDoesNotExist(userNewData.getUsername());
            userUnderChange.setUsername(userNewData.getUsername());
        }

        // Only update the password if it's provided
        if (userNewData.getPassword() != null && !userNewData.getPassword().isEmpty()) {
            userUnderChange.setPassword(passwordEncoder.encode(userNewData.getPassword()));
        }

        // Only update roles if they're provided
        if (userNewData.getRoles() != null) {

            int i = UserUtils.compareRoles(userUnderChange.getRoles());
            log.debug(" i [{}]", i);
            // if 1 decline
            if (i < 1) {
                userUnderChange.setRoles(userNewData.getRoles());
            } else {
                log.debug("The current role is elidible to change higher userUnderChange role [{}]", "todo userUnderChange");

            }

            if (!userUnderChange.getRoles().contains(Role.SUPER_ADMIN)) {
                userNewData.getRoles().remove(Role.SUPER_ADMIN);
            }
        }

        // Only update name if it provided
        if (userNewData.getName() != null || userNewData.getPhones() != null) {
            UserProfile userProfile = userProfileRepository.findByUser(userUnderChange).orElseThrow(() -> new EntityNotFoundException("User profile not found for userUnderChange: " + userUnderChange.getUsername()));

            if (userNewData.getName() != null) {
                userProfile.setName(userNewData.getName());
                log.debug("Updated name of the userUnderChange [{}]", userUnderChange.getUsername());
            }

            if (userNewData.getPhones() != null) {
                userProfile.setPhones(new ArrayList<>(userNewData.getPhones()));
                log.debug("Updated phones of the userUnderChange [{}]", userUnderChange.getUsername());
            }

            userProfileRepository.save(userProfile);
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
        Optional<User> userOp = userRepository.findById(id);
        if (userOp.isEmpty()) {
            log.debug("User with ID [{}] not found", id);
            return;
        }
        User user = userOp.get();

        // delete auth tokens for the user
        authTokenRepository.deleteByUser(user);
        log.debug("Deleted auth tokens for user ID: {}", id);

        // Delete all refresh tokens for the user
        refreshTokenService.deleteByUser(user);
        log.debug("Deleted refresh tokens for user ID: {}", id);

        // Delete all table assignments for the user
        List<TableAssignment> tableAssignments = tableAssignmentRepository.findByWaiter(user);
        if (!tableAssignments.isEmpty()) {
            log.debug("Deleting {} table assignments for user ID: {}", tableAssignments.size(), id);
            // Delete each table assignment individually to avoid issues with bulk deletion
            for (TableAssignment assignment : tableAssignments) {
                tableAssignmentRepository.deleteById(assignment.getId());
            }
        }

        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUser(user);
        userProfileOpt.ifPresent(userProfileRepository::delete);
        userRepository.delete(user);
        log.info("Deleted user ID: {}", user.getId());
    }


    /**
     * Deletes a user by their username.
     *
     * @param username The username of the user to delete.
     * @throws EntityNotFoundException if the user with the given username is not found.
     */
    @Transactional
    public GeneralResponse<?> deleteEateryAdminWithResources(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.debug("User with user name [{}] not found", username);
            return GeneralResponse.builder().message("User not found with username: " + username).success(false).build();
        }

        User user = userOpt.get();

        // First, delete all refresh tokens for the user
        refreshTokenService.deleteByUser(user);
        log.debug("Deleted refresh tokens for user: {}", username);

        // Delete auth token for the user
        authTokenRepository.deleteByUser(user);

        // Now delete the user
        userRepository.delete(user);
        log.debug("User with user name [{}] deleted", username);
        return GeneralResponse.builder().message("User deleted successfully").success(true).build();
    }

    @Transactional
    public ResponseEntity<RegisterResponse> registerAdminAndEatery(RegisterRequest request, boolean isSuperAdmin) {
        return registerUser(request, null, true, isSuperAdmin);
    }

    @Transactional
    public ResponseEntity<RegisterResponse> registerEateryStaff(RegisterRequest request, Long eateryId) {
        return registerUser(request, eateryId, false, false);
    }

    private ResponseEntity<RegisterResponse> registerUser(RegisterRequest request, Long eateryId, boolean isEateryAdmin, boolean isSuperAdmin) {
        validateUserDoesNotExist(request.getUser().getEmail());
        Set<Role> userRoles = request.getUser().getRoles();
        if (userRoles != null && !isSuperAdmin) {
            userRoles.remove(Role.SUPER_ADMIN);
        }
        if (isEateryAdmin) {
            Set<Role> roles = new HashSet<>();
            roles.add(Role.EATERY_ADMIN);
            request.getUser().setRoles(roles);
        }

        User user = createUserEntity(request.getUser().getEmail(), request.getUser().getPassword(), request.getUser().getRoles());
        UserProfile userProfile = userProfileService.createUserProfile(user, request.getUserProfileRequest());

        if (isEateryAdmin && request.getRestaurant() != null) {
            eateryId = createAndLinkEatery(userProfile, request.getRestaurant());
        } else if (eateryId != null) {
            Eatery eatery = userProfileService.addRestaurantToProfile(userProfile, eateryId);
            if (eatery.getOnboardingStatus() != OnboardingStatus.USER_ADDED) {
                eateryLifecycleService.tryPromoteStatus(eatery.getId(), OnboardingStatus.USER_ADDED);
            }
        }

        log.debug("User [{}] successfully created.", user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(user.getId(), eateryId, userProfile.getName(), "User registered successfully", true));
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
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    private User findUserByEateryIdAndUserId(Long eateryId, Long userId) {
        return userRepository.findByEateryIdAndUserId(eateryId, userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    private UserResponse mapToResponse(User user) {
        UserProfile userProfile = userProfileRepository
                .findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found for user: " + user.getUsername()));
        return UserResponse.builder()
                .id(user.getId())
                .eateryIds(userProfile.getEateries().stream().map(Eatery::getId).toList())
                .username(user.getUsername())
                .name(userProfile.getName())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .phone(userProfile.getPhones().stream().toList())
                .registered(userProfile.getCreated().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")))
                .lastLogin(userProfile.getLastLogin() != null
                        ? userProfile.getLastLogin().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))
                        : null)
                .build();
    }

    /**
     * Create a user and profile and sends WELCOME email.
     *
     * @param email    - email
     * @param googleId - googleId
     * @return - User saved in DB
     */
    public User createUserAndProfile(String email, String googleId) {
        User u = new User();
        String name = "Hörmətli müştəri";
        u.setUsername(email);
        u.setGoogleId(googleId);
        u.setPassword(null);
        u.setRoles(Set.of(Role.EATERY_ADMIN));
        User userSaved = userRepository.save(u);
        UserProfile userProfile = userProfileService.createUserProfile(userSaved);
        String locale = userProfile.getLocale();
        if (locale.equals("en")) {
            name = "Dear customer";
        } else if (locale.equals("ru")) {
            name = "Уважаемый клиент";
        }
        String link = "/auth";
        eventPublisher.publishEvent(new UserRegisteredEvent(u.getUsername(),
                locale,
                Map.of("adminName", name,
                        "magicLinkUrl", link)));

        return userSaved;
    }


}
