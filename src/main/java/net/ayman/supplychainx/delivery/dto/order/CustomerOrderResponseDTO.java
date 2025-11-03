package net.ayman.supplychainx.delivery.dto.order;

import net.ayman.supplychainx.delivery.model.Address;
import net.ayman.supplychainx.delivery.model.OrderStatus;

import java.time.LocalDate;

public record CustomerOrderResponseDTO (
        Long id,
        Integer quantity,
        OrderStatus status,
        LocalDate orderDate,
        Double totalAmount,
        Address shippingAddress
) {
}
