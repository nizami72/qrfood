package az.qrfood.backend.eatery.service;

import az.qrfood.backend.category.entity.Category;
import az.qrfood.backend.common.QrCodeGenerator;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.entity.EateryPhone;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.table.entity.TableInEatery;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class EateryService {

    private final EateryRepository eateryRepository;

    @Value("${base.url}")
    private String baseUrl;


    public EateryService(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
    }

    public List<EateryDto> getAllRestaurants() {
        return eateryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EateryDto getRestaurantById(Long id) {
        Eatery restaurant = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));
        return convertToDTO(restaurant);
    }


    public EateryDto createRestaurant(EateryDto restaurantDTO) {

        Eatery r = Util.copyProperties(restaurantDTO, Eatery.class);
        r.setTables(new ArrayList<>());

        r = eateryRepository.save(r);
        populatePhoneEntities(r, restaurantDTO.getPhones());
        populateTables(r, restaurantDTO.getTables());
//        convertToEntity(restaurantDTO, r);
        r = eateryRepository.save(r);
        return convertToDTO(r);
    }

    private EateryDto convertToDTO(Eatery eatery) {

        List<String> phoneNumbers = eatery.getPhones().stream()
                .map(EateryPhone::getPhoneNumber)
                .collect(Collectors.toList());
        EateryDto dto = Util.copyProperties(eatery, EateryDto.class);
        dto.setEateryId(eatery.getId());
        dto.setPhones(phoneNumbers);
        dto.setTables(eatery.getTables().size());

        for (Category menuCategory: eatery.getCategories()) {
            dto.getCategories().add("" + menuCategory.getId());
        }
        dto.setPhones(phoneNumbers);
        return dto;
    }

    private void populatePhoneEntities(Eatery restaurant, List<String> phoneNumbers) {
        List<EateryPhone> restaurantPhones = restaurant.getPhones();
        phoneNumbers.forEach(phoneNumber -> {
            EateryPhone restaurantPhone = new EateryPhone();
            restaurantPhone.setPhoneNumber(phoneNumber);
            restaurantPhone.setRestaurant(restaurant);
            restaurantPhones.add(restaurantPhone);
        });
    }

    private void populateTables(Eatery eatery, int tables) {
        List<TableInEatery> tableList = eatery.getTables();
        AtomicInteger idx = new AtomicInteger();
        List.of(0, tables).forEach(table -> {
            TableInEatery tableInEatery = TableInEatery.builder()
                    .tableNumber(String.valueOf(idx.incrementAndGet()))
                    .restaurant(eatery)
                    .qrCode(populateQrCode(eatery.getId(), idx.get()))
                    .build();
            tableList.add(tableInEatery);
        });
    }

    private byte [] populateQrCode(long eateryId, int tableNumber) {
        String qrContent = baseUrl + "/" + eateryId + "/" + tableNumber;
        try {
            return QrCodeGenerator.generateQRCode(qrContent, 250, 250);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public Long deleteEatery(Long id) {
        Optional<Eatery> eateryOptional = eateryRepository.findById(id);
        if(eateryOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Eatery id [%s] not found", id));
        }
        eateryRepository.deleteById(id);
        return id;
    }
}