package net.ayman.supplychainx.production.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Integer productionTime;
    private Double cost;
    private Double materialCost; // I will handle it after
    private Double profitMargin; // I will handle it after
}
