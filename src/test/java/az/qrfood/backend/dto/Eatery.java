package az.qrfood.backend.dto;

import java.util.List;

/**
 * Represents an Eatery (restaurant) in the JSON structure as a Java record.
 * Records automatically provide a compact way to declare classes that are
 * transparent holders for shallowly immutable data.
 * They automatically generate a canonical constructor, accessor methods (getters),
 * equals(), hashCode(), and toString().
 */
public record Eatery(
    String name,
    String address,
    double geoLat,
    double geoLng,
    int numberOfTables,
    List<String> phones,
    // Note: JSON uses "categories" (singular) for a list, which matches the record component name.
    // @JsonProperty is not strictly needed here as names match, but can be used for clarity
    // or if the JSON key were different (e.g., "categories").
    List<Category> categories
) {
    // Records automatically generate a canonical constructor and accessors.
    // No need for explicit getters, setters, or toString() unless custom logic is required.
    // For example, if you needed custom validation in the constructor:
    // public Eatery {
    //     if (name == null || name.isBlank()) {
    //         throw new IllegalArgumentException("Name cannot be null or blank");
    //     }
    // }
}