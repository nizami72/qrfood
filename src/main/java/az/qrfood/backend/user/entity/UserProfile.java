package az.qrfood.backend.user.entity;

import az.qrfood.backend.eatery.entity.Eatery;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user profile with additional information about the user.
 * <p>
 * This entity is created at the same time as a {@link User} and is linked to
 * restaurants owned by that user. It stores personal details, contact information,
 * and activity timestamps.
 * </p>
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

    /**
     * The full name of the user.
     */
    private String name;

    private String locale;

    /**
     * The {@link User} entity associated with this profile.
     * This is a one-to-one relationship, where each user has one profile.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * A list of phone numbers associated with the user.
     * Stored as a collection of elements in a separate join table.
     */
    @ElementCollection
    @CollectionTable(name = "user_profile_phones", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "phone")
    private List<String> phones = new ArrayList<>();

    /**
     * Flag indicating if the user profile is active.
     * Defaults to {@code true}.
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Timestamp when the user profile was created.
     * This field is set automatically on creation and is not updatable.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    /**
     * Timestamp when the user profile was last updated.
     * This field is set automatically on creation and on every update.
     */
    @Column(nullable = false)
    private LocalDateTime updated;

    /**
     * Timestamp when the user last logged in.
     */
    private LocalDateTime lastLogin;

    /**
     * A list of restaurants (eateries) associated with this user.
     * This is a many-to-many relationship, where each user profile can be associated with multiple eateries,
     * and each eatery can be associated with multiple user profiles.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_profile_restaurants",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    private List<Eatery> eateries = new ArrayList<>();

    /**
     * Pre-persist hook to set the {@code created} and {@code updated} timestamps
     * before the entity is first saved to the database.
     */
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    /**
     * Pre-update hook to update the {@code updated} timestamp
     * before the entity is updated in the database.
     */
    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}
