package net.ayman.supplychainx.delivery.dto.delivery;

import jakarta.validation.constraints.NotNull;

public record UpdateDeliveryStatus(
        @NotNull String status
) {
}
