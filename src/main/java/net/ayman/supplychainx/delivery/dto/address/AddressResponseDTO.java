package net.ayman.supplychainx.delivery.dto.address;

public record AddressResponseDTO (
        Long id,
        String postalCode,
        String region,
        String city,
        String street,
        String country
) {}
