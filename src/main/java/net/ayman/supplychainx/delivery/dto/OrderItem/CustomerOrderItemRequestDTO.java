package net.ayman.supplychainx.delivery.dto.OrderItem;

import jakarta.validation.constraints.NotNull;

public record CustomerOrderItemRequestDTO(
        @NotNull Integer quantity,
        @NotNull Long productId,
        @NotNull Double unitPrice
) {
}
