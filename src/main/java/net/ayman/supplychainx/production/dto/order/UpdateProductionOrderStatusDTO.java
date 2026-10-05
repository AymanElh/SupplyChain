package net.ayman.supplychainx.production.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.ayman.supplychainx.production.model.ProductionStatus;

@Data
public class UpdateProductionOrderStatusDTO {
    @NotNull
    private ProductionStatus status;
}
