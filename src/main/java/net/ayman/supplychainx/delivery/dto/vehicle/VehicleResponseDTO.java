package net.ayman.supplychainx.delivery.dto.vehicle;

public record VehicleResponseDTO(
        Long id,
        String licensePlate,
        String type,
        String model
) {
}
