package net.ayman.supplychainx.delivery.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


public record AddressRequestDTO (
        @NotBlank String country,
        @NotBlank String postalCode,
        @NotBlank String region,
        @NotBlank String city,
        @NotBlank String street
) {
}
