package net.ayman.supplychainx.delivery.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String phone;
    @Column(name = "license_number")
    private String licenseNumber;
    @Column(name = "is_available")
    private Boolean isAvailable = true;

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

}
