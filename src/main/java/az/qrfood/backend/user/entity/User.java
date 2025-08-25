package az.qrfood.backend.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a user entity in the database.
 * <p>
 * This class implements Spring Security's {@link UserDetails} interface,
 * allowing it to be used directly by Spring Security for authentication and authorization.
 * </p>
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the user, which is also used as the login email.
     * This field must be unique and not null.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The encoded password of the user.
     * This field must not be null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The set of roles assigned to the user.
     * <p>
     * Roles are fetched eagerly when the user is loaded.
     * They are stored in a separate join table {@code user_roles}.
     * </p>
     */
    @ElementCollection(fetch = FetchType.EAGER) // Fetch roles immediately when loading the user
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;

    /**
     * The user profile associated with this user.
     * <p>
     * This is a one-to-one relationship, mapped by the "user" field in the {@link UserProfile} entity.
     * Operations on the user (like deletion) will cascade to the user profile.
     * The profile is loaded lazily.
     * </p>
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    /**
     * Returns the collection of authorities (roles) granted to the user.
     *
     * @return A {@link Collection} of {@link GrantedAuthority} objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.name())) // Convert string roles to SimpleGrantedAuthority
                .collect(Collectors.toList());
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return {@code true} if the user's account is valid (not expired), {@code false} otherwise.
     *         In a real application, this might involve checking an expiration commitDate.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // In a real application, there might be account expiration logic
    }

    /**
     * Indicates whether the user's account is locked.
     *
     * @return {@code true} if the user's account is not locked, {@code false} otherwise.
     *         In a real application, this might involve checking for multiple failed login attempts.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // In a real application, there might be account locking logic
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return {@code true} if the user's credentials are valid (not expired), {@code false} otherwise.
     *         In a real application, this might involve checking password expiry policies.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // In a real application, there might be credential expiration logic
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return {@code true} if the user is enabled, {@code false} otherwise.
     *         In a real application, this might involve checking an active status flag.
     */
    @Override
    public boolean isEnabled() {
        return true; // In a real application, there might be account enable/disable logic
    }


    @Override
    public String toString() {
        return "{\"User\":\n{"
                + "        \"roles\":" + getRoles()
                + ",         \"password\":\"" + getPassword() + "\""
                + ",         \"username\":\"" + getUsername() + "\""
                + ",         \"id\":\"" + getId() + "\""
                + "\n}\n}";
    }
}
