package net.ayman.supplychainx.supply.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.ayman.supplychainx.supply.model.OrderStatus;
import net.ayman.supplychainx.validation.OnCreate;

import java.time.LocalDate;
import java.util.List;

@Data
public class SupplierOrderRequestDTO {
    @NotNull(message = "Supplier is required", groups = OnCreate.class)
    private Long supplierId;
    private LocalDate orderDate;
    private OrderStatus status;
    @NotNull(message = "Order have at least one item", groups = OnCreate.class)
    @Valid
    private List<SupplierOrderItemRequestDTO> items;
}
