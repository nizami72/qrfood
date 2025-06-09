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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device")
public class ClientDevice extends BaseEntity {

    private String uuid;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "client_device_orders",
            joinColumns = @JoinColumn(name = "client_device_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Order> orders = new ArrayList<>();
}
