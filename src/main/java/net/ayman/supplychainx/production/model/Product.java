package net.ayman.supplychainx.production.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true, deleted_at = NOW()")
@SQLRestriction("is_deleted = false")
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "production_time")
    private Integer productionTime;
    @Column(nullable = false)
    private Double cost;
    @Column
    private Integer stock = 0;
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "product")
    private List<BillOfMaterial> bills = new ArrayList<>();
    @OneToMany(mappedBy = "product")
    private List<ProductionOrder> productionOrders = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStock(Integer quantity) {
        this.stock += quantity;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean canBeDeleted() {
        return productionOrders == null || productionOrders.isEmpty();
    }

    public Double calculateMaterialCost() {
        if (bills == null || bills.isEmpty()) {
            return 0.0;
        }

        return bills.stream()
                .mapToDouble(bill -> {
                    if(bill.getMaterial() != null && bill.getMaterial().getUnitCost() != null) {
                        return bill.getQuantity() * bill.getMaterial().getUnitCost();
                    }
                    return 0.0;
                })
                .sum();
    }

    public Double calculateProfitMargin() {
        return cost - calculateMaterialCost();
    }

    public boolean hasBom() {
        return bills != null && !bills.isEmpty();
    }
}
