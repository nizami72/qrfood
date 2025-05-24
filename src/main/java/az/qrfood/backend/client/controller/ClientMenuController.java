package az.qrfood.backend.client.controller;

import az.qrfood.backend.client.dto.ClientMenu;
import az.qrfood.backend.client.service.ClientService;
import az.qrfood.backend.eatery.repository.EateryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("${segment.api.client}")
public class ClientMenuController {

    private final ClientService clientService;

    public ClientMenuController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("${component.eatery}/{eatery-id}")
    public ResponseEntity<ClientMenu> clientMenu(@PathVariable("eatery-id") Long eateryId) {
        log.debug("Requested client menu for eatery [{}]", eateryId);
        return ResponseEntity.ok(clientService.getClientMenu(eateryId));
    }

}