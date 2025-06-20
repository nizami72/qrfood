package az.qrfood.backend.client.controller;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.service.ClientDeviceService;
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

@RestController
@RequiredArgsConstructor
@Log4j2
// NAV - Client flow
@RequestMapping("${segment.api.client}")
//@RequestMapping("${segment.api.client.eatery.arg.table.arg}")
public class ClientDeviceController {

    private final ClientDeviceService service;
    private final CategoryService categoryService;
    private final ClientDeviceService clientDeviceService;
    private final TableService tableService;

    @Value("${segment.client.orders}")
    private String componentOrders;
    public static final String DEVICE = "Device_UUID";

    /**
     * GET all categories for eatery(aka menu). NAV-1
     * A client requests menu for eatery and table. If the client has already ordered from this device, he will be
     * redirected to the page where he can see all that orders. If not, the menu will be sent back and a new cookie
     * will be installed when the user confirms new order.

     * @param eateryId eatery ID
     * @param tableId table ID where the client is sitting
     * @param myCookieValue cookie value
     * @return List of categories aka menu
     */
    @GetMapping("${eatery}/{eateryId}${table}/{tableId}")
    public ResponseEntity<List<CategoryDto>> eateryCategories(
            @PathVariable(value = "eateryId") Long eateryId,
            @PathVariable(value = "tableId") Long tableId,
            @CookieValue(value = DEVICE, defaultValue = "") String myCookieValue) {

        log.debug("Request for menu(categories) for eatery [{}] and table [{}]", eateryId, tableId);
        // check if table exists
        if(tableService.findById(tableId).isEmpty()) {
            log.debug("The table doesnt exists [{}]", tableId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // NAV Cookie read
        if (!myCookieValue.isEmpty()) {
            // if cookie found check non completed orders and if any, send him redirect to the page where he can see
            // all that orders
            boolean hasOrders = clientDeviceService.resolveCookie(myCookieValue, tableId);

            if (hasOrders) {
                // redirect to already existing orders if any ordered from this device
                log.debug("Already has active orders redirect to orders page [{}]", tableId);
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .header("X-Redirect-To", componentOrders)
                        .build();
            }
        }

        // no cookie or uncompleted menu; the eatery menu is sent back, new cookie will be
        // installed when the user confirms new order
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }

    /**
     * User already made order and now wants to have one more.

     * @return List of categories aka menu
     */
    @GetMapping("${eatery}/{eateryId}")
    public ResponseEntity<List<CategoryDto>> eateryCategories(@PathVariable(value = "eateryId") Long eateryId){
        log.debug("Find all categories for eatery when table and eatery already known in the device");
        List<CategoryDto> id = categoryService.findAllCategoryForEatery(eateryId);
        return ResponseEntity.ok(id);
    }



    @PostMapping
    public ResponseEntity<ClientDeviceResponseDto> create(@RequestBody ClientDeviceRequestDto dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDeviceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDeviceResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDeviceResponseDto> update(@PathVariable Long id,
                                                          @RequestBody ClientDeviceRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
