package net.ayman.supplychainx.delivery.dto.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.ayman.supplychainx.validation.OnCreate;

public record DriverRequestDTO(
        @NotBlank(groups = OnCreate.class) String name,
        @NotBlank(groups = OnCreate.class) String phone,
        @NotBlank(groups = OnCreate.class) String licenseNumber,
        boolean isAvailable
) {
}
