package az.qrfood.backend.user.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link UserProfile} entities.
 * <p>
 * This service handles the business logic related to user profiles,
 * including creation, updating, and retrieval of profile information,
 * as well as linking profiles to users and restaurants.
 * </p>
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final EateryRepository eateryRepository;

    /**
     * Constructs a UserProfileService with necessary repository dependencies.
     *
     * @param userProfileRepository The repository for UserProfile entities.
     * @param userRepository        The repository for User entities.
     * @param eateryRepository      The repository for Eatery entities.
     */
    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository, EateryRepository eateryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.eateryRepository = eateryRepository;
    }

    /**
     * Creates a new user profile for the given user with provided phone numbers and name.
     *
     * @param user             The {@link User} entity for whom the profile is being created.
     * @param userProfileRequest The {@link RegisterRequest.UserProfileRequest} containing profile details.
     * @return The newly created {@link UserProfile}.
     * @throws IllegalStateException if a user profile already exists for the given user.
     */
    @Transactional
    public UserProfile createUserProfile(User user, RegisterRequest.UserProfileRequest userProfileRequest) {

        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("User profile already exists for user: " + user.getUsername());
        }
        List<String> phones = new ArrayList<>();
        if (userProfileRequest != null && StringUtils.hasText(userProfileRequest.getPhone())) {
            phones.add(userProfileRequest.getPhone());
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        if (userProfileRequest != null && StringUtils.hasText(userProfileRequest.getName())) {
            profile.setName(userProfileRequest.getName());
        }
        profile.setPhones(phones);
        profile.setIsActive(true);
        profile.setCreated(LocalDateTime.now());
        profile.setUpdated(LocalDateTime.now());

        return userProfileRepository.save(profile);
    }

    public UserProfile createUserProfile(User user, String userName) {
        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("User profile already exists for user: " + user.getUsername());
        }
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setIsActive(true);
        profile.setName(userName);
        profile.setCreated(LocalDateTime.now());
        profile.setUpdated(LocalDateTime.now());
        profile.setLocale(LocaleContextHolder.getLocale().getLanguage());
        return userProfileRepository.save(profile);
    }

    public UserProfile createUserProfile(User user) {
        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("User profile already exists for user: " + user.getUsername());
        }
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setIsActive(true);
        profile.setCreated(LocalDateTime.now());
        profile.setUpdated(LocalDateTime.now());
        profile.setLocale(LocaleContextHolder.getLocale().getLanguage());
        return userProfileRepository.save(profile);
    }

    /**
     * Adds a restaurant to the user profile's list of associated restaurants.
     *
     * @param profile      The {@link UserProfile} to which the restaurant will be added.
     * @param restaurantId The ID of the restaurant to add.
     * @throws EntityNotFoundException if the restaurant with the given ID is not found.
     */
    @Transactional
    public Eatery addRestaurantToProfile(UserProfile profile, Long restaurantId) {
        Eatery eatery = eateryRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));

        if (!profile.getEateries().contains(eatery)) {
            profile.getEateries().add(eatery);
            userProfileRepository.save(profile);
        }
        return eatery;
    }

    public void removeRestaurantFromProfile(UserProfile profile, Long eateryId) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + eateryId));

        if (profile.getEateries().contains(eatery)) {
            profile.getEateries().remove(eatery);
            userProfileRepository.save(profile);
        }
    }


    /**
     * Adds a restaurant to the user profile associated with the given {@link UserDetails}.
     * This method retrieves the user and their profile before adding the restaurant.
     *
     * @param userDetails The {@link UserDetails} of the user.
     * @param eateryId    The ID of the eatery to add to the user's profile.
     * @throws EntityNotFoundException if the user, user profile, or eatery is not found.
     */
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
     * Records a user login by updating the {@code lastLogin} timestamp in their profile.
     *
     * @param user The {@link User} entity whose login is to be recorded.
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
     * Finds a user profile by the associated {@link User} entity.
     *
     * @param user The {@link User} entity for which to find the profile.
     * @return An {@link Optional} containing the user profile if found, or empty if not found.
     */
    public Optional<UserProfile> findProfileByUser(User user) {
        return userProfileRepository.findByUser(user);
    }

    /**
     * Finds a user profile by its unique ID.
     *
     * @param id The ID of the user profile to find.
     * @return An {@link Optional} containing the user profile if found, or empty if not found.
     */
    public Optional<UserProfile> findProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    /**
     * Retrieves the current authenticated user's profile from the security context.
     *
     * @return An Optional containing the current user's UserProfile if available.
     */
    public Optional<UserProfile> findCurrentUserProfile() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails)) {
            return Optional.empty();
        }
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .flatMap(userProfileRepository::findByUser);
    }

    public Optional<UserProfile> findProfileByUserWithEateries(User user) {
        return userProfileRepository.findByUserWithEateries(user);
    }
}
