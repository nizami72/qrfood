package az.qrfood.backend.client.controller;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.dto.Menu;
import az.qrfood.backend.client.service.ClientDeviceService;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.service.TableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing client device-related operations and client-facing menu requests.
 * <p>
 * This controller handles requests from client devices, including retrieving menus,
 * managing client device records, and handling cookie-based session management.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Log4j2
// NAV - Client flow
@RequestMapping()
public class ClientDeviceController {

    private final ClientDeviceService service;
    private final CategoryService categoryService;
    private final ClientDeviceService clientDeviceService;
    private final TableService tableService;
    private final EateryService eateryService;

    @Value("${segment.client.orders}")
    private String componentOrders;
    public static final String DEVICE = "Device_UUID";

    /**
     * Retrieves all categories (menu) for a specific eatery and table.
     * <p>
     * This endpoint is designed for clients scanning a QR code at a table.
     * It checks for an existing client device cookie. If a cookie is found and
     * the device has active (uncompleted) orders, the client is redirected to
     * their orders page. Otherwise, the menu for the specified eatery is returned.
     * A new cookie will be installed on the client's device upon order confirmation.
     * </p>
     *
     * @param eateryId      The ID of the eatery.
     * @param tableId       The ID of the table where the client is seated.
     * @return A {@link ResponseEntity} containing a list of {@link CategoryDto} representing the menu,
     *         or a redirect header if active orders exist, or {@code HttpStatus.NOT_FOUND} if the table does not exist.
     */
    @GetMapping("${api.client.eatery.table}")
    public ResponseEntity<Menu> eateryCategories(
            @PathVariable(value = "eateryId") Long eateryId,
            @PathVariable(value = "tableId") Long tableId) {

        log.debug("Request for menu(categories) for eatery [{}] and table [{}]", eateryId, tableId);
        // check if table exists
        Optional<TableDto> table = tableService.findById(tableId);
        if(table.isEmpty()) {
            log.debug("The table doesnt exists [{}]", tableId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        String eateryName = eateryService.getEateryById(eateryId).getName();
        Menu menu = new Menu(eateryId, tableId, eateryName, table.get().number(), id);
        return ResponseEntity.ok(menu);
    }

    /**
     * Retrieves all categories (menu) for a specific eatery when the table and eatery
     * are already known by the client device (e.g., for subsequent orders).
     *
     * @param eateryId The ID of the eatery.
     * @return A {@link ResponseEntity} containing a list of {@link CategoryDto} representing the menu.
     */
    @GetMapping("${api.client.eatery}")
    public ResponseEntity<List<CategoryDto>> eateryCategories(@PathVariable(value = "eateryId") Long eateryId){
        log.debug("Find all categories for eatery when table and eatery already known in the device");
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }



    /**
     * Creates a new client device record.
     *
     * @param dto The {@link ClientDeviceRequestDto} containing the details for the new device.
     * @return A {@link ResponseEntity} containing the {@link ClientDeviceResponseDto} of the newly created device
     *         with {@code HttpStatus.CREATED}.
     */
    @PostMapping("${api.client}")
    public ResponseEntity<ClientDeviceResponseDto> create(@RequestBody ClientDeviceRequestDto dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific client device by its ID.
     *
     * @param id The ID of the client device to retrieve.
     * @return A {@link ResponseEntity} containing the {@link ClientDeviceResponseDto} of the found device.
     */
    @GetMapping("${api.client.id}")
    public ResponseEntity<ClientDeviceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Retrieves all client device records.
     *
     * @return A {@link ResponseEntity} containing a list of {@link ClientDeviceResponseDto} objects.
     */
    @GetMapping("${api.client}")
    public ResponseEntity<List<ClientDeviceResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Updates an existing client device record.
     *
     * @param id  The ID of the client device to update.
     * @param dto The {@link ClientDeviceRequestDto} containing the updated details.
     * @return A {@link ResponseEntity} containing the {@link ClientDeviceResponseDto} of the updated device.
     */
    @PutMapping("${api.client.id}")
    public ResponseEntity<ClientDeviceResponseDto> update(@PathVariable Long id,
                                                          @RequestBody ClientDeviceRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Deletes a client device record by its ID.
     *
     * @param id The ID of the client device to delete.
     * @return A {@link ResponseEntity} with no content and {@code HttpStatus.NO_CONTENT}.
     */
    @DeleteMapping("${api.client.id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
