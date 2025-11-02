package net.ayman.supplychainx.production.model;

import jakarta.persistence.*;
import lombok.Data;
import net.ayman.supplychainx.supply.model.RawMaterial;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bill_of_materials")
@SQLDelete(sql = "UPDATE bill_of_materials SET is_deleted = false, deleted_at = NOW()")
@SQLRestriction("is_deleted = false")
@Data
public class BillOfMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "material_id")
    private RawMaterial material;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Time stamps
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
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // Business logic
    public Double calculateTotalCost() {
        if(material == null || material.getUnitCost() == null) {
            return 0.0;
        }
        return quantity * material.getUnitCost();
    }

}
