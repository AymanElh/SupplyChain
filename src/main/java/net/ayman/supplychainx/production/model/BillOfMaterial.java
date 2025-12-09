package net.ayman.supplychainx.production.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
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

    // delete the relationship of material_id and replace with a simple reference id
    @Column(name = "material_id", nullable = false)
    private Long materialId;

    // snapshot of the price of raw material on the bill of material order
    @Column(name = "material_price", precision = 10, scale = 10)
    private BigDecimal priceAtOrder;

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
    public BigDecimal calculateTotalCost() {
        if(priceAtOrder == null) {
            return BigDecimal.ZERO;
        }
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }

}
