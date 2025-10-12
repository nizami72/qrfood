package az.qrfood.backend.auth.util;

import az.qrfood.backend.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for working with JWT (JSON Web Tokens).
 * <p>
 * This class is responsible for generating, extracting information from, and validating JWT tokens.
 * It uses a secret key for signing and verifying tokens, and manages token expiration.
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * The secret key used for signing JWTs.
     * It should be sufficiently long (at least 256 bits) and securely stored.
     * In a production environment, it's recommended to retrieve this from environment variables
     * or a secure configuration management system.
     */
    @Value("${jwt.secret:yourVerySecretKeyThatIsAtLeast256BitsLongAndShouldBeChangedInProduction}")
    private String secret;

    /**
     * The expiration time for JWT tokens in milliseconds (e.g., 10 hours).
     */
    @Value("${jwt.expiration}")
    private long expiration; // 10 hours

    /**
     * Generates the signing key from the secret string.
     *
     * @return The {@link Key} used for signing and verifying JWTs.
     */
    private Key getSigningKey() {
        // Generate the key from the secret string
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration commitDate from the given JWT token.
     *
     * @param token The JWT token.
     * @return The expiration {@link Date} of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          The JWT token.
     * @param claimsResolver A function to resolve the desired claim from the {@link Claims}.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token The JWT token.
     * @return A {@link Claims} object containing all claims from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the given JWT token has expired.
     *
     * @param token The JWT token.
     * @return {@code true} if the token's expiration commitDate is before the current commitDate, {@code false} otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the given user details, including an eatery ID.
     * <p>
     * This method creates a token that includes the user's roles and a specific
     * eatery ID as claims. This is useful for scoping user access to a particular eatery.
     * </p>
     *
     * @param userDetails The {@link UserDetails} object containing user information.
     * @param eateryId    The ID of the active eatery to be included in the token claims.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails, Long eateryId) {
        Map<String, Object> claims = new HashMap<>();
        // Add user roles to claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        // Add the active eatery ID to claims if provided
        if (eateryId != null) {
            claims.put("eateryId", eateryId);
        }
        // Add the user's ID if userDetails is a User entity
        if (userDetails instanceof User) {
            claims.put("userId", ((User) userDetails).getId());
        }
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the specified claims and subject.
     *
     * @param claims  The claims to be included in the token.
     * @param subject The subject of the token (typically the username).
     * @return The created JWT token string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the given JWT token against the provided user details.
     * <p>
     * The token is considered valid if its username matches the user details' username
     * and the token has not expired.
     * </p>
     *
     * @param token       The JWT token to validate.
     * @param userDetails The {@link UserDetails} object to validate against.
     * @return {@code true} if the token is valid for the given user and has not expired, {@code false} otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
