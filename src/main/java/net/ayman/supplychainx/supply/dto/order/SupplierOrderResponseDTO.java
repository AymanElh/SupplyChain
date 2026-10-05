package net.ayman.supplychainx.supply.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ayman.supplychainx.supply.model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderResponseDTO {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private OrderStatus status;
    private LocalDate orderDate;
    private List<SupplierOrderItemResponseDTO> items;
}
