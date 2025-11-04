package net.ayman.supplychainx.delivery.dto.OrderItem;

import jakarta.validation.constraints.NotNull;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;

import java.util.List;

public record CustomerOrderItemResponseDTO(
        Long id,
        Integer quantity,
        ProductResponseDTO product,
        Double unitPrice,
        Double subTotal
) {
}
