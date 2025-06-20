package az.qrfood.backend.qr.controller;

import az.qrfood.backend.qr.service.QrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("${segment.api.qr-code}")
@Tag(name = "QR Code Management", description = "API endpoints for generating QR codes for eatery tables")
public class QrController {

    private final QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    /**
     * Generate a QR code image for a specific eatery table.
     *
     * @param eateryId the ID of the eatery
     * @param tableNumber the table number
     * @return QR code image as byte array
     */
    @Operation(summary = "Generate QR code for table", description = "Generates a QR code image for a specific eatery table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery or table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "${eatery}/{eatery}${table}/{table}")
    public ResponseEntity<byte[]> getQrImage(@PathVariable("eatery") Long eateryId,
                                             @PathVariable("table") Integer tableNumber) {
        log.debug("Requested QR image for eatery [{}] and table [{}]", eateryId, tableNumber);
        byte[] qrCode = qrService.getQrImage(eateryId, tableNumber);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

}
