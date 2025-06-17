package az.qrfood.backend.user.service;

import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private Set<String> roles;

    @BeforeEach
    void setUp() {
        // Set up test data
        roles = new HashSet<>();
        roles.add("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRoles(roles);

        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setPassword("password");
        userRequest.setRoles(roles);
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertFalse(response.isHasProfile());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.createUser(userRequest));
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("testuser", responses.get(0).getUsername());
        assertEquals(roles, responses.get(0).getRoles());
        assertFalse(responses.get(0).isHasProfile());

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertFalse(response.isHasProfile());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertFalse(response.isHasProfile());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername("testuser"));
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void updateUser_Success() {
        // Arrange
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setPassword("newpassword");
        updateRequest.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("encodedNewPassword");
        updatedUser.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserResponse response = userService.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("updateduser", response.getUsername());
        assertEquals(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")), response.getRoles());
        assertFalse(response.isHasProfile());

        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("updateduser");
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, userRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_UsernameAlreadyExists() {
        // Arrange
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("existinguser");
        updateRequest.setPassword("newpassword");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void mapToResponse_WithProfile() {
        // Arrange
        User userWithProfile = new User();
        userWithProfile.setId(1L);
        userWithProfile.setUsername("testuser");
        userWithProfile.setRoles(roles);
        
        UserProfile profile = new UserProfile();
        userWithProfile.setProfile(profile);

        // Act
        UserResponse response = userService.getUserById(1L); // This will call mapToResponse internally

        // Prepare for the internal call
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithProfile));

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertTrue(response.isHasProfile());

        verify(userRepository).findById(1L);
    }
}