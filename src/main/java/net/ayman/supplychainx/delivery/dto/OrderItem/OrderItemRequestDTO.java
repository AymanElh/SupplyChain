package net.ayman.supplychainx.delivery.dto.OrderItem;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull Integer productId,
        @NotNull Integer quantity
) {
}
