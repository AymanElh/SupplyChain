package net.ayman.supplychainx.supply.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderItemResponseDTO {
    private Long id;
    private Long materialId;
    private String materialName;
    private Integer quantity;
    private Double unitPrice;
    private Double subTotal;
}
