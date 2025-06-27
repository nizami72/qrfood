package az.qrfood.backend.client.entity;

import az.qrfood.backend.common.entity.BaseEntity;
import az.qrfood.backend.order.entity.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client device that interacts with the QR Food Order system.
 * <p>
 * This entity stores information about a unique client device, identified by a UUID,
 * and maintains a history of orders placed from that device.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device")
public class ClientDevice extends BaseEntity {

    /**
     * The universally unique identifier (UUID) for the client device.
     * This serves as a unique identifier for the device across sessions.
     */
    private String uuid;

    /**
     * A list of orders placed from this client device.
     * <p>
     * This is a many-to-many relationship managed through a join table
     * {@code client_device_orders}. The {@code CascadeType.ALL} ensures that
     * all operations (persist, merge, remove, refresh, detach) are cascaded
     * to the associated orders.
     * </p>
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "client_device_orders",
            joinColumns = @JoinColumn(name = "client_device_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Order> orders = new ArrayList<>();
}
