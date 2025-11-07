package net.ayman.supplychainx.delivery.dto.delivery;

import net.ayman.supplychainx.delivery.model.DeliveryStatus;

import java.time.LocalDate;

public record DeliveryResponseDTO(
    Long id,
    OrderInfo order,
    DriverInfo driver,
    VehicleInfo vehicle,
    DeliveryStatus status,
    LocalDate deliveryDate
) {

    public record OrderInfo(Long id, String name, Double totalAmount) {}
    public record DriverInfo(Long id, String name, String phone) {}
    public record VehicleInfo(Long id, String licensePlate) {}
}
