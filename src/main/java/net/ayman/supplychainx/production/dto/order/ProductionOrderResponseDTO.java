package net.ayman.supplychainx.production.dto.order;

import lombok.Data;
import net.ayman.supplychainx.production.model.ProductionStatus;

import java.time.LocalDate;

@Data
public class ProductionOrderResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer priority;
    private ProductionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
