package az.qrfood.backend.auth.service;

import az.qrfood.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * <p>
 * This service is responsible for loading user-specific data during the authentication process.
 * It retrieves user details from the {@link UserRepository}.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the CustomUserDetailsService with a UserRepository dependency.
     *
     * @param userRepository The repository for accessing user data.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user information by their username (email).
     * <p>
     * This method is called by Spring Security during the authentication process
     * to retrieve user details. It queries the {@link UserRepository} for a user
     * matching the provided username.
     * </p>
     *
     * @param username The username (email) of the user to load.
     * @return A {@link UserDetails} object representing the loaded user.
     * @throws UsernameNotFoundException if a user with the specified username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user not found: " + username));
    }

}
