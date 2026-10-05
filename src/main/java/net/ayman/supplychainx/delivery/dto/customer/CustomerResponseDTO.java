package net.ayman.supplychainx.delivery.dto.customer;

import net.ayman.supplychainx.delivery.dto.address.AddressResponseDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerResponseDTO(
        Long id,
        String name,
        String phone,
        String email,
        List<AddressResponseDTO> addresses,
        List<CustomerOrderResponseDTO> orders,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
