package net.ayman.supplychainx.supply.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class SupplierOrderRequestDTO {
    @NotNull(message = "Supplier is required")
    private Long supplierId;
    private LocalDate orderDate;
    @NotNull(message = "Order have at least one item")
    @Valid
    private List<SupplierOrderItemRequestDTO> items;
}
