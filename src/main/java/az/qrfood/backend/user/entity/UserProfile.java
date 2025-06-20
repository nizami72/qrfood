package az.qrfood.backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user profile with additional information about the user.
 * Created at the same time as User and linked to restaurants owned by the user.
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    /**
     * Unique identifier for the user profile.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * The user associated with this profile.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * List of phone numbers associated with the user.
     */
    @ElementCollection
    @CollectionTable(name = "user_profile_phones", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "phone")
    private List<String> phones = new ArrayList<>();

    /**
     * Flag indicating if the user profile is active.
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Timestamp when the user profile was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    /**
     * Timestamp when the user profile was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updated;

    /**
     * Timestamp when the user last logged in.
     */
    private LocalDateTime lastLogin;

    /**
     * List of restaurant IDs owned by this user.
     */
    @ElementCollection
    @CollectionTable(name = "user_profile_restaurants", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "restaurant_id")
    private List<Long> restaurantIds = new ArrayList<>();

    /**
     * Pre-persist hook to set created and updated timestamps before saving.
     */
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    /**
     * Pre-update hook to update the updated timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}