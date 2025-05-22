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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public EateryDto getEateryById(Long id) {
        Eatery restaurant = eateryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Eatery with id %s not found", id)));
        return convertToDTO(restaurant);
    }


    public EateryDto createEatery(EateryDto restaurantDTO) {

        Eatery r = Util.copyProperties(restaurantDTO, Eatery.class);
        r = eateryRepository.save(r);
        populatePhoneEntities(r, restaurantDTO.getPhones());
        populateTables(r, restaurantDTO.getTablesAmount());
        r = eateryRepository.save(r);
        return convertToDTO(r);
    }

    private EateryDto convertToDTO(Eatery eatery) {

        EateryDto dto = Util.copyProperties(eatery, EateryDto.class);
        eatery.getPhones().forEach(phone -> {
            dto.getPhones().add(phone.getPhoneNumber());
        });

//        List<TableInEatery> tables = eatery.getTables();
//        if(!tables.isEmpty()) {
        eatery.getTables().forEach(table -> {
            dto.getTableIds().add(table.getId());
        });
//        }


        dto.setEateryId(eatery.getId());
        dto.setTablesAmount(eatery.getTables().size());

        List<Category> categories = eatery.getCategories();
        if (!categories.isEmpty()) {
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
        AtomicInteger idx = new AtomicInteger();
        IntStream.range(0, tables).forEach(table -> {
            TableInEatery tableInEatery = TableInEatery.builder()
                    .tableNumber(String.valueOf(idx.incrementAndGet()))
                    .restaurant(eatery)
                    .qrCode(populateQrCode(eatery.getId(), idx.get()))
                    .build();
            tableList.add(tableInEatery);
        });
    }

    private byte[] populateQrCode(long eateryId, int tableNumber) {
        String qrContent = baseUrl + "/" + eateryId + "/" + tableNumber;
        try {
            return QrCodeGenerator.generateQRCode(qrContent, 250, 250);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public Long deleteEatery(Long id) {
        Optional<Eatery> eateryOptional = eateryRepository.findById(id);
        if (eateryOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Eatery id [%s] not found", id));
        }
        eateryRepository.deleteById(id);
        return id;
    }
}