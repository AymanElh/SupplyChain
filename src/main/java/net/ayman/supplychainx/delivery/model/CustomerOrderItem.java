package net.ayman.supplychainx.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import net.ayman.supplychainx.production.model.Product;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_order_items")
@Data
public class CustomerOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @Column(name = "unit_price")
    private Double unitPrice;
    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateSubTotal() {
        this.subTotal = this.quantity * this.unitPrice;
    }
}
