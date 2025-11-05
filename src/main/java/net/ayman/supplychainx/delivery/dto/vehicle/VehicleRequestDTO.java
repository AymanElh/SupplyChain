package net.ayman.supplychainx.delivery.dto.vehicle;

import jakarta.validation.constraints.NotBlank;

public record VehicleRequestDTO(
        @NotBlank(message = "License plate cannot be blank")
        String licensePlate,
        String type,
        String model
) {
}
