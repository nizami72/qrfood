package az.qrfood.backend.user.service;

import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing User entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user.
     *
     * @param request the user request
     * @return the created user response
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles() != null ? request.getRoles() : new HashSet<>());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /**
     * Get all users.
     *
     * @return list of user responses
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    /**
     * Get all users that belong to a specific eatery.
     *
     * @param id the eatery ID
     * @return list of user responses for users belonging to the eatery
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Long id) {
        // Find all user profiles associated with the given restaurant ID
        List<UserProfile> profiles = userProfileRepository.findByRestaurantId(id);

        // Extract the users from the profiles and map them to UserResponse objects
        return profiles.stream()
                .map(UserProfile::getUser)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return the user response
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    /**
     * Get a user by username.
     *
     * @param username the username
     * @return the user response
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return mapToResponse(user);
    }

    /**
     * Update a user.
     *
     * @param id the user ID
     * @param request the user request
     * @return the updated user response
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists: " + request.getUsername());
        }

        user.setUsername(request.getUsername());

        // Only update password if it's provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Only update roles if they're provided
        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    /**
     * Delete a user.
     *
     * @param id the user ID
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Map a User entity to a UserResponse DTO.
     *
     * @param user the user entity
     * @return the user response DTO
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles(),
                user.getProfile() != null
        );
    }
}
