package az.qrfood.backend.client.service;

import static az.qrfood.backend.client.controller.ClientDeviceController.DEVICE;

import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.entity.ClientDevice;
import az.qrfood.backend.client.repository.ClientDeviceRepository;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.entity.Order;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing {@link ClientDevice} entities and their associated operations.
 * <p>
 * This service handles the creation, retrieval, updating, and deletion of client device records.
 * It also manages the creation of UUID-based cookies for client devices and tracks their orders.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ClientDeviceService {

    @Value("${client.cookie.expiration}")
    int cookieExpiraionTime;
    private final ClientDeviceMapper mapper;
    private final ClientDeviceRepository clientDeviceRepository;

    /**
     * Creates a new cookie with a unique UUID for a client device and associates it with an order.
     * <p>
     * This method generates a new UUID, creates a {@link Cookie} object, and persists
     * a new {@link ClientDevice} entity linked to the provided order.
     * </p>
     *
     * @param order The {@link Order} to associate with the new client device.
     * @return A {@link Cookie} object containing the generated UUID.
     */
    public Cookie createCookieUuid(Order order) {
        // NAV Cookie install
        Cookie cookie = new Cookie(DEVICE, String.valueOf(UUID.randomUUID()));
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiraionTime);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        // cookie.setDomain("example.com");
        // .sameSite("Strict")

        ClientDevice device = new ClientDevice();
        device.setUuid(cookie.getValue());
        device.getOrders().add(order);
        clientDeviceRepository.save(device);
        log.debug("Created order [{}] for device [{}]", order.getId(), cookie.getValue());

        return cookie;

    }


    /**
     * Creates a new {@link ClientDevice} based on the provided DTO.
     *
     * @param dto The {@link ClientDeviceRequestDto} containing the details for the new device.
     * @return A {@link ClientDeviceResponseDto} representing the newly created device.
     */
    public ClientDeviceResponseDto create(ClientDeviceRequestDto dto) {
        ClientDevice device = mapper.toEntity(dto);
        return mapper.toDto(clientDeviceRepository.save(device));
    }

    /**
     * Retrieves a {@link ClientDevice} by its unique identifier.
     *
     * @param id The ID of the client device to retrieve.
     * @return A {@link ClientDeviceResponseDto} representing the found device.
     * @throws EntityNotFoundException if no client device with the given ID is found.
     */
    public ClientDeviceResponseDto getById(Long id) {
        ClientDevice device = clientDeviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientDevice not found"));
        return mapper.toDto(device);
    }

    /**
     * Retrieves all {@link ClientDevice} records in the system.
     *
     * @return A list of {@link ClientDeviceResponseDto} representing all client devices.
     */
    public List<ClientDeviceResponseDto> getAll() {
        return clientDeviceRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing {@link ClientDevice} with new information.
     *
     * @param id  The ID of the client device to update.
     * @param dto The {@link ClientDeviceRequestDto} containing the updated details.
     * @return A {@link ClientDeviceResponseDto} representing the updated device.
     * @throws EntityNotFoundException if no client device with the given ID is found.
     */
    public ClientDeviceResponseDto update(Long id, ClientDeviceRequestDto dto) {
        ClientDevice device = clientDeviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientDevice not found"));
        mapper.updateEntity(device, dto);
        return mapper.toDto(clientDeviceRepository.save(device));
    }

    /**
     * Deletes a {@link ClientDevice} by its unique identifier.
     *
     * @param id The ID of the client device to delete.
     */
    public void delete(Long id) {
        clientDeviceRepository.deleteById(id);
    }

    /**
     * Resolves a client device based on its UUID cookie and checks if it has any active orders.
     *
     * @param cookie The UUID string from the client's cookie.
     * @return {@code true} if the client device exists and has at least one active order (status {@code NEW}),
     *         {@code false} otherwise.
     * @throws EntityNotFoundException if no client device with the given UUID is found.
     */
    public boolean resolveCookie(String cookie) {
        Optional<ClientDevice> op = clientDeviceRepository.findByUuid(cookie);
        if(op.isEmpty()){
            throw new EntityNotFoundException("The entity of ClientDevice not found" + cookie);
        }
        List<Order> orders = op.get().getOrders();

        return hasActive(orders);
    }

    /**
     * Checks if any of the provided orders have a status of {@code NEW}.
     *
     * @param orders A list of {@link Order} objects.
     * @return {@code true} if at least one order has a status of {@code NEW}, {@code false} otherwise.
     */
    private boolean hasActive(List<Order> orders) {
        return orders.stream()
                .anyMatch(o -> o.getStatus().equals(OrderStatus.CREATED));
    }

    /**
     * Adds an order to an existing client device identified by its UUID.
     * <p>
     * This method finds a {@link ClientDevice} by its UUID, adds the provided order
     * to its list of orders, and saves the updated entity.
     * </p>
     *
     * @param deviceUuid  The UUID string identifying the client device.
     * @param order The {@link Order} to add to the client device.
     * @return A {@link Cookie} object containing the UUID (for consistency with createCookieUuid).
     * @throws EntityNotFoundException if no client device with the given UUID is found.
     */
    public Cookie resolveCookieUuid(String deviceUuid, Order order) {
        if (StringUtils.hasText(deviceUuid)) {
            return resolveExistingOrCreateNewCookie(deviceUuid, order);
        }
        log.debug("No device cookie found, creating new client device");
        return createCookieUuid(order);
    }

    private Cookie addOrderToExistingDevice(ClientDevice device, String uuid, Order order) {

        device.getOrders().add(order);
        clientDeviceRepository.save(device);
        log.debug("Added order [{}] to existing device [{}]", order.getId(), uuid);

        // Return the existing cookie for consistency with createCookieUuid
        Cookie cookie = new Cookie(DEVICE, uuid);
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiraionTime);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        return cookie;
    }

    private Cookie resolveExistingOrCreateNewCookie(String deviceUuid, Order order) {
        return clientDeviceRepository.findByUuid(deviceUuid)
                .map(device -> {
                    log.debug("Existing cookie found in request and database: {}, adding order to existing device", deviceUuid);
                    return addOrderToExistingDevice(device, deviceUuid, order);
                })
                .orElseGet(() -> {
                    log.debug("No cookie found in DB for deviceUuid={}, creating new client device", deviceUuid);
                    return createCookieUuid(order);
                });
    }
}
