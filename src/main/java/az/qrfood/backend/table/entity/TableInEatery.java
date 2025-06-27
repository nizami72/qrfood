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

/**
 * Represents a physical table within an {@link Eatery}.
 * <p>
 * This entity stores details about a table, including its number, number of seats,
 * any specific notes, its current status, and associated QR code for ordering.
 * It also maintains a list of orders placed from this table.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "table_in_eatery")
@Builder
public class TableInEatery {

    /**
     * The unique identifier for the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The eatery to which this table belongs.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "eatery_id", nullable = false)
    private Eatery eatery;

    /**
     * The unique number or identifier for the table within the eatery.
     */
    @Column(name = "table_number")
    private String tableNumber;

    /**
     * The number of seats available at this table.
     */
    @Column(name = "seats")
    private int seats;

    /**
     * Any specific notes or remarks about the table (e.g., "near window", "smoking area").
     */
    @Column(name = "note")
    private String note;

    /**
     * The current status of the table (e.g., AVAILABLE, OCCUPIED, DIRTY).
     * This maps to {@link TableStatus}.
     */
    @Column(name = "status")
    private TableStatus status;

    /**
     * The QR code associated with this table for ordering.
     * This is a one-to-one relationship, where each table has one QR code.
     * The {@code CascadeType.ALL} ensures that operations on the table
     * (like deletion) cascade to the associated QR code.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qr_code_id", referencedColumnName = "id")
    private QrCode qrCode;

    /**
     * A list of orders placed from this table.
     * This is a one-to-many relationship, where one table can have multiple orders.
     * The {@code mappedBy = "table"} indicates that the "table" field in the {@link Order} entity
     * is the owning side of this relationship.
     */
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Order> orders;

    /**
     * Provides a string representation of the TableInEatery object, primarily its ID.
     *
     * @return A string representation of the table's ID.
     */
    public String toString() {
        return "ID = " + id;
    }
}
