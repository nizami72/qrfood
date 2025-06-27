package az.qrfood.backend.eatery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a phone number associated with an {@link Eatery}.
 * <p>
 * This entity allows a single eatery to have multiple contact phone numbers.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eatery_phone")
public class EateryPhone {

    /**
     * The unique identifier for the phone number entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The actual phone number, stored in the "phone" column.
     */
    @Column(name = "phone", nullable = false)
    private String phoneNumber;

    /**
     * The eatery to which this phone number belongs.
     * <p>
     * This is a many-to-one relationship, linking this phone number back to its parent eatery.
     * The {@link JsonIgnore} annotation is crucial to prevent infinite recursion
     * during JSON serialization when fetching an Eatery and its phones.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "eatery_id", nullable = false)
    @JsonIgnore
    private Eatery restaurant;

    /**
     * Returns the phone number as the default string representation of this object.
     *
     * @return The phone number string.
     */
    @Override
    public String toString() {
        return phoneNumber;
    }
}