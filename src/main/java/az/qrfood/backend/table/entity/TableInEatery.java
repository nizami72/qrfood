package az.qrfood.backend.table.entity;

import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.qr.entity.QrCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "table_in_eatery")
@Builder
public class TableInEatery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eatery_id", nullable = false)
    private Eatery eatery;

    @Column(name = "table_number")
    private String tableNumber;

    @Column(name = "seats")
    private int seats;

    @Column(name = "note")
    private String note;

    @Column(name = "status")
    private TableStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qr_code_id", referencedColumnName = "id")
    private QrCode qrCode;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Order> orders;

    public String toString() {
        return "ID = " + id;
    }
}
