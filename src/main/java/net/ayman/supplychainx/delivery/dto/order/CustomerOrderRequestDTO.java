package net.ayman.supplychainx.delivery.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import net.ayman.supplychainx.delivery.dto.OrderItem.OrderItemRequestDTO;

import java.util.List;

public record CustomerOrderRequestDTO(
        @NotNull Long customerId,
        @NotNull @Min(value = 1) Integer quantity,
        @NotNull Integer addressId,
        @NotNull List<OrderItemRequestDTO> orders
) {
}
