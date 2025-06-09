package az.qrfood.backend.client.service;


import static az.qrfood.backend.client.controller.ClientDeviceController.DEVICE;

import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.entity.ClientDevice;
import az.qrfood.backend.client.repository.ClientDeviceRepository;
import az.qrfood.backend.order.entity.Order;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClientDeviceService {

    private final ClientDeviceRepository repository;
    private final ClientDeviceMapper mapper;

    public Cookie createCookieUuid(Order order) {

        // NAV Cookie install
        Cookie cookie = new Cookie(DEVICE, String.valueOf(UUID.randomUUID()));
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        // cookie.setDomain("example.com");



//                .sameSite("Strict")
//                .build();


        ClientDevice device = new ClientDevice();
        device.setUuid(cookie.getValue());
        device.getOrders().add(order);
        repository.save(device);
        log.debug("Created order [{}] for device [{}]", order.getId(), cookie.getValue());

        return cookie;

    }


    public ClientDeviceResponseDto create(ClientDeviceRequestDto dto) {
        ClientDevice device = mapper.toEntity(dto);
        return mapper.toDto(repository.save(device));
    }

    public ClientDeviceResponseDto getById(Long id) {
        ClientDevice device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientDevice not found"));
        return mapper.toDto(device);
    }

    public List<ClientDeviceResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ClientDeviceResponseDto update(Long id, ClientDeviceRequestDto dto) {
        ClientDevice device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientDevice not found"));
        mapper.updateEntity(device, dto);
        return mapper.toDto(repository.save(device));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean resolveCookie(String cookie, Long tableId) {


        return true;
    }
}
