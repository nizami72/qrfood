package az.qrfood.backend.qr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents a generated QR code in the system.
 * <p>
 * This entity stores the QR code image as a byte array, along with its
 * validity period. It can be associated with a specific table.
 * </p>
 */
@Entity
@Data
@AllArgsConstructor
@Table(name = "qr_code")
public class QrCode {

    /**
     * The unique identifier for the QR code.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The QR code image data stored as a byte array (BLOB).
     * This field is mandatory.
     */
    @Column(name = "qr_code", columnDefinition = "BLOB", nullable = false)
    @Lob // Indicates that this field should be stored as a large object
    private byte[] qrCodeAsBytes;

    /**
     * The timestamp from which this QR code is valid.
     * Defaults to the current time when the entity is created.
     */
    private LocalDateTime validFrom;

    /**
     * The timestamp until which this QR code is valid.
     * Can be null if the QR code has no expiration.
     */
    private LocalDateTime validTo;

    private String content;

    /**
     * Default constructor. Initializes {@code validFrom} to the current time.
     */
    public QrCode() {
        this.validFrom = LocalDateTime.now();
    }

    //    @OneToOne
//    @JoinColumn(name = "table_id", nullable = false, unique = true) // unique обязательно!
//    private TableInEatery table;
}
