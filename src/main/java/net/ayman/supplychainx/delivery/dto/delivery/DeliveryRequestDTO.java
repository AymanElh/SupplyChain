package net.ayman.supplychainx.delivery.dto.delivery;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DeliveryRequestDTO(
    @NotNull Long orderId,
    @NotNull Long driverId,
    @NotNull Long vehicleId,
    @NotNull @Future LocalDate deliveryDate
) {
}
