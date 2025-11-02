package net.ayman.supplychainx.production.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_orders")
@SQLDelete(sql = "UPDATE production_orders SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Data
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionStatus status;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column
    private Integer priority = 5;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

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
        if (status == null) {
            status = ProductionStatus.IN_WAITING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeCancelled() {
        return this.status == ProductionStatus.IN_WAITING;
    }

    public boolean canBeStart() {
        return this.status == ProductionStatus.IN_WAITING;
    }

    public void startProduction() {
        status = ProductionStatus.IN_PRODUCTION;
        startDate = LocalDate.now();
    }
}
