package net.ayman.supplychainx.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@SQLDelete(sql = "UPDATE customer_orders SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Data
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'PENDING'")
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date")
    private LocalDate orderDate = LocalDate.now();

    @Column(name = "total_amount")
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<CustomerOrderItem> items = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
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
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void startOrder() {
        this.orderDate = LocalDate.now();
        this.status = OrderStatus.IN_PREPARATION;
    }

    public boolean canBeDelivered() {
        return status == OrderStatus.READY;
    }

    public void addOrderItem(CustomerOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void calculateTotalAmount() {
        items.forEach(CustomerOrderItem::calculateSubTotal);
        this.totalAmount = items.stream().mapToDouble(CustomerOrderItem::getSubTotal).sum();
    }
}
