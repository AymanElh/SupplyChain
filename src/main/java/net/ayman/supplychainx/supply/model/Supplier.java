package net.ayman.supplychainx.supply.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true)
    private String email;


    @Column
    private Double rating;

    @Column(name = "lead_time")
    private Integer leadTime;

    @OneToMany(mappedBy = "supplier")
    private List<SupplierOrder> orders;

    @ManyToMany(mappedBy = "suppliers")
    private List<RawMaterial> materials;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasActiveOrders() {
        if (orders == null || orders.isEmpty()) {
            return false;
        }

        return orders
                .stream()
                .anyMatch(order -> order.getStatus() != OrderStatus.RECEIVED);
    }
}
