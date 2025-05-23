package az.qrfood.backend.qr.service;

import az.qrfood.backend.common.QrCodeGenerator;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.qr.entity.QrCode;
import az.qrfood.backend.table.entity.TableInEatery;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class QrService {

    private final EateryRepository eateryRepository;

    @Value("${component.eatery}")
    private String componentEatery;
    @Value("${component.table}")
    private String componentTable;
    @Value("${segment.qr.redirect}")
    private String segmentQrRedirect;

    public QrService(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
    }

    public QrCode createQrCodeEntity(long eateryId, int tableNumber, String url) {
        QrCode code = new QrCode();

        String qrContent = String.format(segmentQrRedirect, eateryId, tableNumber);


        try {
            code.setQrCodeAsBytes(QrCodeGenerator.generateQRCode(qrContent, 250, 250));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
        Util.saveLinkToFile("http://" + qrContent);
        log.debug("Redirect link [{}]", "http://" + qrContent);

        return code;
    }

    public byte[] getQrImage(Long eateryId, Integer tableNumber) {
        Eatery eatery = eateryRepository.findById(eateryId).orElseThrow(EntityNotFoundException::new);
        List<TableInEatery> tableInEateryList = eatery.getTables();
        Optional<TableInEatery> op = tableInEateryList.stream()
                .filter(t -> t.getTableNumber().equals(String.valueOf(tableNumber)))
                .findFirst();
        if(op.isEmpty()) {
            throw new EntityNotFoundException(String.format("Qr code for eatery [%s] and table [%s} could not be found",
                    eateryId, tableNumber));
        }
        return op.get().getQrCode().getQrCodeAsBytes();
    }

}