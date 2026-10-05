package net.ayman.supplychainx.supply.service;

import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierOrderService {
    SupplierOrderResponseDTO createOrder(SupplierOrderRequestDTO dto);
    void deleteOrder(Long id);
    Page<SupplierOrderResponseDTO> getAll(Pageable pageable);
    SupplierOrderResponseDTO getById(Long id);

    SupplierOrderResponseDTO updateOrderStatus(Long id, OrderStatus status);
    SupplierOrderResponseDTO receiveOrder(Long id);
}
