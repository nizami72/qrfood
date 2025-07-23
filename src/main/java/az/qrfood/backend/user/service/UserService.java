package az.qrfood.backend.user.service;

import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
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

    /**
     * Creates a new user based on the provided request.
     * <p>
     * This method checks for existing usernames and encrypts the password before saving.
     * </p>
     *
     * @param request The {@link UserRequest} containing user details.
     * @return A {@link UserResponse} representing the newly created user.
     * @throws IllegalStateException if a user with the same username already exists.
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        validateUserDoesNotExist(request.getUsername());
        User savedUser = createUserEntity(request.getUsername(), request.getPassword(), request.getRoles());
        return mapToResponse(savedUser);
    }

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
        User user = findUserById(id);
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

    /**
     * Updates an existing user.
     * <p>
     * This method allows updating the username, password (if provided), roles, and user profile name.
     * </p>
     *
     * @param id      The ID of the user to update.
     * @param request The {@link UserRequest} containing the updated user data.
     * @return A {@link UserResponse} representing the updated user.
     * @throws EntityNotFoundException if the user with the given ID is not found.
     * @throws IllegalStateException   if the new username already exists.
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserById(id);

        if (!user.getUsername().equals(request.getUsername())) {
            validateUserDoesNotExist(request.getUsername());
            user.setUsername(request.getUsername());
        }

        // Only update password if it's provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Only update roles if they're provided
        if (request.getRoles() != null) {
            if(!user.getRoles().contains(Role.SUPER_ADMIN)) {
                request.getRoles().remove(Role.SUPER_ADMIN);
            }
            user.setRoles(request.getRoles());
        }

        // Only update name if it provided
        if (request.getName() != null) {
            UserProfile userProfile = userProfileRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("User profile not found for user: " + user.getUsername()));
            userProfile.setName(request.getName());
            userProfileRepository.save(userProfile);
            log.debug("Updated name of the user [{}]", user.getUsername());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws EntityNotFoundException if the user with the given ID is not found.
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
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
        eateryDto.setNumberOfTables(1);
        eateryDto.setOwnerProfileId(profile.getId());
        Long eateryId = eateryService.createEatery(eateryDto);
        userProfileService.addRestaurantToProfile(profile, eateryId);
        return eateryId;
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
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
