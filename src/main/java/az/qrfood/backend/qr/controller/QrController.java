package az.qrfood.backend.qr.controller;

import az.qrfood.backend.qr.service.QrService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("${segment.api.qr}")
public class QrController {

    private final QrService qrService;

    @Value("${segment.api.qr.redirect}")
    private String segmentRedirect;
    @Value("${segment.api.category}")
    private String segmentApiCategory;


    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @GetMapping(value = "${component.eatery}/{eatery}${component.table}/{table}")
    public ResponseEntity<byte []> getQrImage(@PathVariable("eatery") Long eateryId,
                                              @PathVariable("table") Integer tableNumber) {
        log.debug("Requested QR image for eatery [{}] and table [{}]", eateryId, tableNumber);
        byte [] qrCode = qrService.getQrImage(eateryId, tableNumber);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @GetMapping("${component.qr.redirect}${component.eatery}/{eateryId}${component.table}/{tableNumber}")
    public ResponseEntity<Void> redirectWithCookies(
            @PathVariable String eateryId,
            @PathVariable String tableNumber,
            HttpServletResponse response) {

        // Устанавливаем куки
        ResponseCookie eateryCookie = ResponseCookie.from("eatery", eateryId)
                .path("/")
                .httpOnly(false)
                .build();

        ResponseCookie tableCookie = ResponseCookie.from("table", tableNumber)
                .path("/")
                .httpOnly(false)
                .build();

        response.addHeader("Set-Cookie", eateryCookie.toString());
        response.addHeader("Set-Cookie", tableCookie.toString());

        // Отправляем редирект
        response.setStatus(HttpServletResponse.SC_FOUND); // 302
        response.setHeader("Location", String.format(segmentRedirect, eateryId));

        return ResponseEntity.status(HttpServletResponse.SC_FOUND).build();
    }

}