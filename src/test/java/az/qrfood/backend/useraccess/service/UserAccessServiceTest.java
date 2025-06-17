package az.qrfood.backend.useraccess.service;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.useraccess.dto.UserAccessRequest;
import az.qrfood.backend.useraccess.dto.UserAccessResponse;
import az.qrfood.backend.useraccess.entity.UserAccess;
import az.qrfood.backend.useraccess.repository.UserAccessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccessServiceTest {

    @Mock
    private UserAccessRepository userAccessRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EateryRepository eateryRepository;

    @InjectMocks
    private UserAccessService userAccessService;

    private User user;
    private Eatery eatery;
    private UserAccess userAccess;
    private UserAccessRequest userAccessRequest;

    @BeforeEach
    void setUp() {
        // Set up test data
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        eatery = new Eatery();
        eatery.setId(1L);
        eatery.setName("Test Eatery");

        userAccess = new UserAccess();
        userAccess.setId(1L);
        userAccess.setUser(user);
        userAccess.setEatery(eatery);
        userAccess.setRole(Role.WAITER);

        userAccessRequest = new UserAccessRequest();
        userAccessRequest.setUserId(1L);
        userAccessRequest.setEateryId(1L);
        userAccessRequest.setRole(Role.WAITER);
    }

    @Test
    void createUserAccess_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eateryRepository.findById(1L)).thenReturn(Optional.of(eatery));
        when(userAccessRepository.findByUserAndEateryAndRole(user, eatery, Role.WAITER)).thenReturn(Optional.empty());
        when(userAccessRepository.save(any(UserAccess.class))).thenReturn(userAccess);

        // Act
        UserAccessResponse response = userAccessService.createUserAccess(userAccessRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals(1L, response.getEateryId());
        assertEquals("Test Eatery", response.getEateryName());
        assertEquals(Role.WAITER, response.getRole());

        verify(userRepository).findById(1L);
        verify(eateryRepository).findById(1L);
        verify(userAccessRepository).findByUserAndEateryAndRole(user, eatery, Role.WAITER);
        verify(userAccessRepository).save(any(UserAccess.class));
    }

    @Test
    void createUserAccess_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userAccessService.createUserAccess(userAccessRequest));
        verify(userRepository).findById(1L);
        verify(eateryRepository, never()).findById(anyLong());
        verify(userAccessRepository, never()).save(any(UserAccess.class));
    }

    @Test
    void createUserAccess_EateryNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eateryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userAccessService.createUserAccess(userAccessRequest));
        verify(userRepository).findById(1L);
        verify(eateryRepository).findById(1L);
        verify(userAccessRepository, never()).save(any(UserAccess.class));
    }

    @Test
    void createUserAccess_AlreadyExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eateryRepository.findById(1L)).thenReturn(Optional.of(eatery));
        when(userAccessRepository.findByUserAndEateryAndRole(user, eatery, Role.WAITER)).thenReturn(Optional.of(userAccess));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userAccessService.createUserAccess(userAccessRequest));
        verify(userRepository).findById(1L);
        verify(eateryRepository).findById(1L);
        verify(userAccessRepository).findByUserAndEateryAndRole(user, eatery, Role.WAITER);
        verify(userAccessRepository, never()).save(any(UserAccess.class));
    }

    @Test
    void getAllUserAccess_Success() {
        // Arrange
        List<UserAccess> userAccessList = Arrays.asList(userAccess);
        when(userAccessRepository.findAll()).thenReturn(userAccessList);

        // Act
        List<UserAccessResponse> responses = userAccessService.getAllUserAccess();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(1L, responses.get(0).getUserId());
        assertEquals("testuser", responses.get(0).getUsername());
        assertEquals(1L, responses.get(0).getEateryId());
        assertEquals("Test Eatery", responses.get(0).getEateryName());
        assertEquals(Role.WAITER, responses.get(0).getRole());

        verify(userAccessRepository).findAll();
    }

    @Test
    void getUserAccessById_Success() {
        // Arrange
        when(userAccessRepository.findById(1L)).thenReturn(Optional.of(userAccess));

        // Act
        UserAccessResponse response = userAccessService.getUserAccessById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals(1L, response.getEateryId());
        assertEquals("Test Eatery", response.getEateryName());
        assertEquals(Role.WAITER, response.getRole());

        verify(userAccessRepository).findById(1L);
    }

    @Test
    void getUserAccessById_NotFound() {
        // Arrange
        when(userAccessRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userAccessService.getUserAccessById(1L));
        verify(userAccessRepository).findById(1L);
    }

    @Test
    void updateUserAccess_Success() {
        // Arrange
        when(userAccessRepository.findById(1L)).thenReturn(Optional.of(userAccess));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eateryRepository.findById(1L)).thenReturn(Optional.of(eatery));
        when(userAccessRepository.findByUserAndEateryAndRole(user, eatery, Role.WAITER)).thenReturn(Optional.of(userAccess));
        when(userAccessRepository.save(any(UserAccess.class))).thenReturn(userAccess);

        // Act
        UserAccessResponse response = userAccessService.updateUserAccess(1L, userAccessRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals(1L, response.getEateryId());
        assertEquals("Test Eatery", response.getEateryName());
        assertEquals(Role.WAITER, response.getRole());

        verify(userAccessRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(eateryRepository).findById(1L);
        verify(userAccessRepository).findByUserAndEateryAndRole(user, eatery, Role.WAITER);
        verify(userAccessRepository).save(any(UserAccess.class));
    }

    @Test
    void deleteUserAccess_Success() {
        // Arrange
        when(userAccessRepository.existsById(1L)).thenReturn(true);

        // Act
        userAccessService.deleteUserAccess(1L);

        // Assert
        verify(userAccessRepository).existsById(1L);
        verify(userAccessRepository).deleteById(1L);
    }

    @Test
    void deleteUserAccess_NotFound() {
        // Arrange
        when(userAccessRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userAccessService.deleteUserAccess(1L));
        verify(userAccessRepository).existsById(1L);
        verify(userAccessRepository, never()).deleteById(anyLong());
    }
}