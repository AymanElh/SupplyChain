package net.ayman.supplychainx.supply.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "raw_materials")
@SQLDelete(sql = "UPDATE raw_materials SET is_deleted = true, deleted_at = NOW()")
@SQLRestriction("is_deleted = false")
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column
    private Integer stock;
    @Column
    private Integer stockMin;
    @Column
    private String unit;

    @ManyToMany
    @JoinTable(
            name = "material_suppliers",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    @OneToMany(mappedBy = "rawMaterial")
    private List<SupplierOrderItem> orderItems;

    @Column(name = "unit_cost")
    private Double unitCost;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStock(Integer quantity) {
        this.stock += quantity;
        if (this.stock < 0) {
            this.stock = 0;
        }
    }

    public boolean isMaterialInOrdering() {
        return orderItems != null && !orderItems.isEmpty();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
