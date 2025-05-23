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

@Entity
@Data
@AllArgsConstructor
@Table(name = "qr_code")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code", columnDefinition = "BLOB", nullable = false)
    @Lob
    private byte[] qrCodeAsBytes;

    public QrCode() {
        this.validFrom = LocalDateTime.now();
    }

    //    @OneToOne
//    @JoinColumn(name = "table_id", nullable = false, unique = true) // unique обязательно!
//    private TableInEatery table;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;
}
