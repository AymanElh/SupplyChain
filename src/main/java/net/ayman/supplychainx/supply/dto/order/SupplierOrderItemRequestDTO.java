package net.ayman.supplychainx.supply.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderItemRequestDTO {
    @NotNull(message = "Material id is required")
    private Long materialId;
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit pice must be positive")
    private Double unitPrice;
}
