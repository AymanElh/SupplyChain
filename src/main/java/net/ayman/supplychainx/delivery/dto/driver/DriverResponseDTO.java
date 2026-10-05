package net.ayman.supplychainx.delivery.dto.driver;

public record DriverResponseDTO(
        Long id,
        String name,
        String phone,
        String licenseNumber,
        Boolean isAvailable
) {
}
