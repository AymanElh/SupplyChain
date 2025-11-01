package net.ayman.supplychainx.production.model;

import jakarta.persistence.*;
import lombok.Data;
import net.ayman.supplychainx.supply.model.RawMaterial;

import java.util.List;

@Entity
@Table(name = "bill_of_materials")
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

}
