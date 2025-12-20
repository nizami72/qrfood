package az.qrfood.backend.eatery.service;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.service.StorageService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.entity.EateryPhone;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing {@link Eatery} entities.
 * <p>
 * This class encapsulates the business logic related to eateries,
 * including CRUD operations, phone number management, table creation,
 * and conversion between DTOs and entities.
 * </p>
 */
@Service
@Log4j2
public class EateryService {

    private final EateryRepository eateryRepository;
    private final TableService tableService;
    private final StorageService storageService;
    private final UserProfileRepository userProfileRepository;
    private final EateryLifecycleService eateryLifecycleService;

    /**
     * Constructs an EateryService with necessary dependencies.
     *
     * @param eateryRepository      The repository for Eatery entities.
     * @param tableService          The service for managing tables within an eatery.
     * @param storageService        The service for handling storage operations (e.g., creating eatery folders).
     * @param userProfileRepository The repository for UserProfile entities.
     */
    public EateryService(EateryRepository eateryRepository,
                         TableService tableService,
                         StorageService storageService,
                         UserProfileRepository userProfileRepository,
                         EateryLifecycleService eateryLifecycleService) {
        this.eateryRepository = eateryRepository;
        this.tableService = tableService;
        this.storageService = storageService;
        this.userProfileRepository = userProfileRepository;
        this.eateryLifecycleService = eateryLifecycleService;
    }

    /**
     * Retrieves all eateries in the system.
     *
     * @return A list of {@link EateryDto} representing all eateries.
     */
    public List<EateryDto> getAllRestaurants() {
        return eateryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single eatery by its ID.
     *
     * @param id The ID of the eatery to retrieve.
     * @return An {@link EateryDto} representing the found eatery.
     * @throws EntityNotFoundException if no eatery with the given ID is found.
     */
    public EateryDto getEateryById(Long id) {
        Eatery restaurant = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));
        return convertToDTO(restaurant);
    }

    /**
     * Retrieves an eatery by the ID of its owner.
     * <p>
     * Note: This method currently uses the eatery ID as the owner ID.
     * It might need refinement if the owner concept is distinct from the eatery ID.
     * </p>
     *
     * @param id The ID of the eatery owner.
     * @return An {@link EateryDto} representing the found eatery.
     * @throws EntityNotFoundException if no eatery with the given ID is found.
     */
    public EateryDto getEateryByOwnerId(Long id) {
        Eatery restaurant = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));
        return convertToDTO(restaurant);
    }

    /**
     * Creates a new eatery based on the provided DTO.
     * <p>
     * This method handles the creation of the eatery entity, populates its
     * associated phone numbers and tables, saves it to the database, and
     * creates a dedicated storage folder for the new eatery.
     * </p>
     *
     * @param restaurantDTO The {@link EateryDto} containing the details for the new eatery.
     * @return The ID of the newly created eatery.
     */
    public Long createEatery(EateryDto restaurantDTO) {

        Eatery eatery = Util.copyProperties(restaurantDTO, Eatery.class);
        eatery = eateryRepository.save(eatery);
        populatePhoneEntities(eatery, restaurantDTO.getPhones());
        eatery = eateryRepository.save(eatery);
        if(eatery.getOnboardingStatus() == null) {
            eateryLifecycleService.promoteStatus(eatery.getId(), OnboardingStatus.EATERY_CREATED);
        }
        EateryDto dto = convertToDTO(eatery);
        storageService.createEateryFolder(dto.getId());
        return eatery.getId();
    }

    /**
     * Converts an {@link Eatery} entity to an {@link EateryDto}.
     * <p>
     * This method maps the fields from the entity to the DTO, including
     * extracting phone numbers, table IDs, and category IDs.
     * </p>
     *
     * @param eatery The {@link Eatery} entity to convert.
     * @return The converted {@link EateryDto}.
     */
    private EateryDto convertToDTO(Eatery eatery) {
        EateryDto dto = Util.copyProperties(eatery, EateryDto.class);
        eatery.getPhones().forEach(phone -> {
            dto.getPhones().add(phone.getPhoneNumber());
        });

        eatery.getTables().forEach(table -> {
            dto.getTableIds().add(table.getId());
        });

        dto.setId(eatery.getId());
        dto.setNumberOfTables(eatery.getTables().size());


        List<Category> categories = eatery.getCategories();
        if (categories != null && !categories.isEmpty()) {
            eatery.getCategories().forEach(category -> {
                dto.getCategoryIds().add(category.getId());
            });
        }
        dto.setOwnerMail(
                eatery.getUserProfiles().stream()
                        .map(UserProfile::getUser)
                        .map(User::getUsername)
                        .findFirst()
                        .orElse("Mail Not Found")

        );


        return dto;
    }

    /**
     * Populates the phone entities for a given eatery based on a list of phone numbers.
     *
     * @param eatery       The {@link Eatery} entity to which the phone numbers belong.
     * @param phoneNumbers A list of phone number strings.
     */
    private void populatePhoneEntities(Eatery eatery, List<String> phoneNumbers) {
        List<EateryPhone> restaurantPhones = eatery.getPhones();
        phoneNumbers.forEach(phoneNumber -> {
            EateryPhone restaurantPhone = new EateryPhone();
            restaurantPhone.setPhoneNumber(phoneNumber);
            restaurantPhone.setRestaurant(eatery);
            restaurantPhones.add(restaurantPhone);
        });
    }

    /**
     * Deletes an eatery by its ID.
     *
     * @param id The ID of the eatery to delete.
     * @return The ID of the deleted eatery.
     * @throws EntityNotFoundException if no eatery with the given ID is found.
     */
    public Long deleteEatery(Long id) {
        Optional<Eatery> eateryOptional = eateryRepository.findById(id);
        if (eateryOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Eatery id [%s] not found", id));
        }
        eateryRepository.deleteById(id);
        return id;
    }

    /**
     * Updates an existing eatery with new information.
     * <p>
     * This method updates basic fields, phone numbers, and potentially the number of tables.
     * </p>
     *
     * @param id        The ID of the eatery to update.
     * @param eateryDTO The {@link EateryDto} containing the updated details.
     * @return The ID of the updated eatery.
     * @throws EntityNotFoundException if no eatery with the given ID is found.
     */
    public Long updateEatery(Long id, EateryDto eateryDTO) {
        Eatery existingEatery = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));

        // Update basic fields
        existingEatery.setName(eateryDTO.getName());
        existingEatery.setAddress(eateryDTO.getAddress());
        existingEatery.setGeoLat(eateryDTO.getGeoLat());
        existingEatery.setGeoLng(eateryDTO.getGeoLng());

        // Update phone numbers
        updatePhoneNumbers(existingEatery, eateryDTO.getPhones());

        // todo is it neededUpdate tables if the amount has changed
        if (existingEatery.getTables().size() != eateryDTO.getNumberOfTables()) {
            log.warn("Eatery tables amount changed");
//            updateTables(existingEatery, eateryDTO.getTablesAmount());
        }

        // Save the updated eatery
        existingEatery = eateryRepository.save(existingEatery);

        return existingEatery.getId();
    }

    /**
     * Finds eateries associated with a specific user profile ID.
     *
     * @param id The ID of the user profile.
     * @return A list of {@link EateryDto} associated with the user profile.
     * @throws EntityNotFoundException if the user profile with the given ID is not found.
     */
    public List<EateryDto> findEateriesByUserProfileId(Long id) {
        UserProfile userProfile = userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("User profile with id not found: " + id));
        if (userProfile.getUser().getRoles().contains(Role.SUPER_ADMIN)) {
            log.debug("Super admin is trying to find all eateries");
            return getAllRestaurants();
        } else {
            return userProfile.getEateries().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }


    /**
     * Updates the phone numbers for an existing eatery.
     * <p>
     * This method clears all existing phone numbers for the eatery and then
     * populates them with the new list provided.
     * </p>
     *
     * @param eatery          The {@link Eatery} entity to update.
     * @param newPhoneNumbers A list of new phone number strings.
     */
    private void updatePhoneNumbers(Eatery eatery, List<String> newPhoneNumbers) {
        // Clear existing phone numbers
        eatery.getPhones().clear();

        // Add new phone numbers
        populatePhoneEntities(eatery, newPhoneNumbers);
    }

}
