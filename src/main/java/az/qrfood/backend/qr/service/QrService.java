package az.qrfood.backend.qr.service;

import az.qrfood.backend.common.QrCodeGenerator;
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

/**
 * Service class for managing QR code generation and retrieval.
 * <p>
 * This service handles the creation of QR code entities, generating QR code images
 * based on eatery and table information, and retrieving existing QR code images.
 * </p>
 */
@Service
@Log4j2
public class QrService {

    private final EateryRepository eateryRepository;

    @Value("${segment.menu}")
    private String segmentMenu;

    /**
     * Constructs a QrService with an EateryRepository dependency.
     *
     * @param eateryRepository The repository for Eatery entities.
     */
    public QrService(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
    }

    /**
     * Creates a new {@link QrCode} entity and generates its corresponding QR code image.
     * <p>
     * The QR code content is a URL that links to the menu for a specific eatery and table.
     * The generated QR code image is stored as a byte array within the {@link QrCode} entity.
     * </p>
     *
     * @param eateryId  The ID of the eatery.
     * @param tableId   The ID of the table.
     * @return The newly created {@link QrCode} entity with the generated QR code image.
     * @throws RuntimeException if there is an error during QR code image generation.
     */
    public QrCode createQrCodeEntity(long eateryId, Long tableId) {
        QrCode code = new QrCode();

        String qrContent = String.format(segmentMenu, eateryId, tableId, "false");

        // NAV - generating QR code
        try {
            code.setQrCodeAsBytes(QrCodeGenerator.generateQRCode(qrContent, 250, 250));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
        code.setContent(qrContent);
//        String s = qrContent.replace("192.168.1.76:8081", "127.0.0.1:5173");
//        Util.saveLinkToFile(s);
//        log.debug("Menu link [{}]", s);

        return code;
    }

    /**
     * Retrieves the QR code image (as a byte array) for a specific eatery and table number.
     *
     * @param eateryId    The ID of the eatery.
     * @param tableNumber The number of the table.
     * @return A byte array representing the QR code image.
     * @throws EntityNotFoundException if the eatery or the table/QR code for the given eatery and table number is not found.
     */
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
