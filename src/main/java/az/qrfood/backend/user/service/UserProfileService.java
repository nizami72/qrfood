package az.qrfood.backend.user.service;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing UserProfileRequest entities.
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user profile for the given user with phone numbers.
     *
     * @param user The user entity
     * @return The created user profile
     */
    @Transactional
    public UserProfile createUserProfile(User user, RegisterRequest.UserProfileRequest userProfileRequest) {

        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("User profile already exists for user: " + user.getUsername());
        }
        List<String> phones = new ArrayList<>();
        if (StringUtils.hasText(userProfileRequest.getPhone())) {
            phones.add(userProfileRequest.getPhone());
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(userProfileRequest.getName());
        profile.setPhones(phones);
        profile.setIsActive(true);
        profile.setCreated(LocalDateTime.now());
        profile.setUpdated(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * Adds a restaurant ID to the user profile's list of owned restaurants.
     *
     * @param profile      The user profile
     * @param restaurantId The restaurant ID to add
     */
    @Transactional
    public void addRestaurantToProfile(UserProfile profile, Long restaurantId) {
        if (!profile.getRestaurantIds().contains(restaurantId)) {
            profile.getRestaurantIds().add(restaurantId);
        }
    }

    @Transactional
    public void addRestaurantToProfile(UserDetails userDetails, Long eateryId) {
        String userName = userDetails.getUsername();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userName));
        UserProfile userProfile = findProfileByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found: " + user));
        addRestaurantToProfile(userProfile, eateryId);
    }

    /**
     * Records a user login by updating the lastLogin timestamp.
     *
     * @param user The user entity
     */
    @Transactional
    public void recordLogin(User user) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        if (profileOpt.isPresent()) {
            UserProfile profile = profileOpt.get();
            profile.setLastLogin(LocalDateTime.now());
            userProfileRepository.save(profile);
        }
    }

    /**
     * Finds a user profile by user.
     *
     * @param user The user entity
     * @return Optional containing the user profile if found
     */
    public Optional<UserProfile> findProfileByUser(User user) {
        return userProfileRepository.findByUser(user);
    }

    /**
     * Finds a user profile by ID.
     *
     * @param id The profile ID
     * @return Optional containing the user profile if found
     */
    public Optional<UserProfile> findProfileById(Long id) {
        return userProfileRepository.findById(id);
    }
}