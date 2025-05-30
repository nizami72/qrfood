package az.qrfood.backend.eatery.service;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.common.QrCodeGenerator;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.service.StorageService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.entity.EateryPhone;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.user.profile.UserProfile;
import az.qrfood.backend.user.profile.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EateryService {

    private final EateryRepository eateryRepository;
    private final TableService tableService;
    private final StorageService storageService;
    private final UserProfileRepository userProfileRepository;

    public EateryService(EateryRepository eateryRepository, 
                         TableService tableService, 
                         StorageService storageService,
                         UserProfileRepository userProfileRepository) {
        this.eateryRepository = eateryRepository;
        this.tableService = tableService;
        this.storageService = storageService;
        this.userProfileRepository = userProfileRepository;
    }

    public List<EateryDto> getAllRestaurants() {
        return eateryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EateryDto getEateryById(Long id) {
        Eatery restaurant = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));
        return convertToDTO(restaurant);
    }


    public Long createEatery(EateryDto restaurantDTO) {
        Eatery r = Util.copyProperties(restaurantDTO, Eatery.class);

        // Set owner profile if provided
        if (restaurantDTO.getOwnerProfileId() != null) {
            userProfileRepository.findById(restaurantDTO.getOwnerProfileId())
                .ifPresent(r::setOwner);
        }

        r = eateryRepository.save(r);
        populatePhoneEntities(r, restaurantDTO.getPhones());
        populateTables(r, restaurantDTO.getTablesAmount());
        r = eateryRepository.save(r);
        EateryDto dto = convertToDTO(r);
        storageService.createEateryFolder(dto.getEateryId());
        return r.getId();
    }

    private EateryDto convertToDTO(Eatery eatery) {
        EateryDto dto = Util.copyProperties(eatery, EateryDto.class);
        eatery.getPhones().forEach(phone -> {
            dto.getPhones().add(phone.getPhoneNumber());
        });

        eatery.getTables().forEach(table -> {
            dto.getTableIds().add(table.getId());
        });

        dto.setEateryId(eatery.getId());
        dto.setTablesAmount(eatery.getTables().size());

        // Set owner profile ID if available
        if (eatery.getOwner() != null) {
            dto.setOwnerProfileId(eatery.getOwner().getId());
        }

        List<Category> categories = eatery.getCategories();
        if (categories != null && !categories.isEmpty()) {
            eatery.getCategories().forEach(category -> {
                dto.getCategoryIds().add(category.getId());
            });
        }
        return dto;
    }

    private void populatePhoneEntities(Eatery eatery, List<String> phoneNumbers) {
        List<EateryPhone> restaurantPhones = eatery.getPhones();
        phoneNumbers.forEach(phoneNumber -> {
            EateryPhone restaurantPhone = new EateryPhone();
            restaurantPhone.setPhoneNumber(phoneNumber);
            restaurantPhone.setRestaurant(eatery);
            restaurantPhones.add(restaurantPhone);
        });
    }

    private void populateTables(Eatery eatery, int tables) {
        List<TableInEatery> tableList = eatery.getTables();
        AtomicInteger idx = new AtomicInteger(1);
        IntStream.range(0, tables).forEach(table -> {
            tableList.add(tableService.createTableInEatery(eatery, idx.getAndIncrement()));
        });
    }


    public Long deleteEatery(Long id) {
        Optional<Eatery> eateryOptional = eateryRepository.findById(id);
        if (eateryOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Eatery id [%s] not found", id));
        }
        eateryRepository.deleteById(id);
        return id;
    }

    public Long updateEatery(Long id, EateryDto eateryDTO) {
        Eatery existingEatery = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));

        // Update basic fields
        existingEatery.setName(eateryDTO.getName());
        existingEatery.setAddress(eateryDTO.getAddress());
        existingEatery.setGeoLat(eateryDTO.getGeoLat());
        existingEatery.setGeoLng(eateryDTO.getGeoLng());

        // Update owner profile if provided
        if (eateryDTO.getOwnerProfileId() != null) {
            userProfileRepository.findById(eateryDTO.getOwnerProfileId())
                .ifPresent(existingEatery::setOwner);
        }

        // Update phone numbers
        updatePhoneNumbers(existingEatery, eateryDTO.getPhones());

        // Update tables if the amount has changed
        if (existingEatery.getTables().size() != eateryDTO.getTablesAmount()) {
            updateTables(existingEatery, eateryDTO.getTablesAmount());
        }

        // Save the updated eatery
        existingEatery = eateryRepository.save(existingEatery);

        return existingEatery.getId();
    }

    private void updatePhoneNumbers(Eatery eatery, List<String> newPhoneNumbers) {
        // Clear existing phone numbers
        eatery.getPhones().clear();

        // Add new phone numbers
        populatePhoneEntities(eatery, newPhoneNumbers);
    }

    private void updateTables(Eatery eatery, int newTableCount) {
        int currentTableCount = eatery.getTables().size();

        if (newTableCount > currentTableCount) {
            // Add more tables
            AtomicInteger idx = new AtomicInteger(currentTableCount + 1);
            IntStream.range(0, newTableCount - currentTableCount).forEach(i -> {
                eatery.getTables().add(tableService.createTableInEatery(eatery, idx.getAndIncrement()));
            });
        } else if (newTableCount < currentTableCount) {
            // Remove excess tables
            List<TableInEatery> tablesToKeep = eatery.getTables().subList(0, newTableCount);
            eatery.getTables().clear();
            eatery.getTables().addAll(tablesToKeep);
        }
    }
}
