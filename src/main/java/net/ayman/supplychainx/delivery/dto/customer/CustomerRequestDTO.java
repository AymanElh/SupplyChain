package net.ayman.supplychainx.delivery.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.ayman.supplychainx.delivery.dto.address.AddressRequestDTO;
import net.ayman.supplychainx.validation.OnCreate;

import java.util.List;

public record CustomerRequestDTO (
        @NotBlank(groups = OnCreate.class) String name,
        @NotBlank(groups = OnCreate.class) String phone,
        String email,
        List<AddressRequestDTO> addresses
) {}