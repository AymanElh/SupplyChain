package net.ayman.supplychainx.delivery.dto.driver;

public record DriverResponseDTO(
        String name,
        String phone,
        String licenseNumber,
        Boolean isAvailable
) {
}
