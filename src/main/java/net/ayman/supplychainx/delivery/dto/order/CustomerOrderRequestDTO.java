package net.ayman.supplychainx.delivery.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import net.ayman.supplychainx.delivery.dto.OrderItem.CustomerOrderItemRequestDTO;
import net.ayman.supplychainx.delivery.dto.OrderItem.CustomerOrderItemResponseDTO;
import net.ayman.supplychainx.validation.OnCreate;

import java.util.List;

public record CustomerOrderRequestDTO(
        @NotNull(groups = OnCreate.class) Long customerId,
        @NotNull Long addressId,
        @NotNull List<CustomerOrderItemRequestDTO> orderItems
) {
}
