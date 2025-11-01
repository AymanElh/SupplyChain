package net.ayman.supplychainx.production.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "production_orders")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ProductionStatus status;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column
    private Integer priority;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
