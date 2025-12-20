package az.qrfood.backend.qr.controller;

import az.qrfood.backend.constant.ApiRoutes;
import az.qrfood.backend.qr.service.QrService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for managing QR code generation and retrieval.
 * <p>
 * This controller provides API endpoints for generating QR code images
 * for specific eatery tables.
 * </p>
 */
@Log4j2
@RestController
@Tag(name = "QR Code Management", description = "API endpoints for generating QR codes for eatery tables")
public class QrController {

    private final QrService qrService;

    /**
     * Constructs a QrController with a QrService dependency.
     *
     * @param qrService The service for handling QR code business logic.
     */
    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    /**
     * Generates and retrieves a QR code image for a specific eatery table.
     * <p>
     * The QR code content will typically link to the menu for the specified table.
     * </p>
     *
     * @param eateryId    The ID of the eatery.
     * @param tableId The number of the table for which to generate the QR code.
     * @return A {@link ResponseEntity} containing the QR code image as a byte array (PNG format).
     */
    @Operation(summary = "Generate QR code for table", description = "Generates a QR code image for a specific eatery table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery or table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication,#eateryId, 'EATERY_ADMIN')")
    @GetMapping(value = ApiRoutes.QR_CODE)
    public ResponseEntity<byte[]> getQrImage(@PathVariable("eateryId") Long eateryId,
                                             @PathVariable("tableId") Long tableId) {
        log.debug("Requested QR image for eatery [{}] and table [{}]", eateryId, tableId);
        byte[] qrCode = qrService.getQrImage(eateryId, tableId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }


    @Operation(summary = "Get all QR contents", description = "Returns list of all QR code content strings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.isSuperAdmin(authentication)")
    @GetMapping(value = ApiRoutes.QR_CODE_CONTENTS)
    public ResponseEntity<List<String>> getQrContents(@PathVariable("eateryId") Long eateryId) {
        List<String> contents = qrService.getAllQrContents(eateryId);
        return ResponseEntity.ok(contents);
    }
}
