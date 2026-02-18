package net.ayman.supplychainx.production.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Integer productionTime;
    private Double cost;
    private BigDecimal materialCost; // I will handle it after
    private BigDecimal profitMargin; // I will handle it after
    private Integer stock;
    private Boolean hasBom;
}
