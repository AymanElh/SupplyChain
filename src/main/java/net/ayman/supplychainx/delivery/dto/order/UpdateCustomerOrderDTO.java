package net.ayman.supplychainx.delivery.dto.order;

import jakarta.validation.constraints.NotNull;
import net.ayman.supplychainx.delivery.model.OrderStatus;

public record UpdateCustomerOrderDTO(
        @NotNull OrderStatus status
) {
}
