package net.ayman.supplychainx.production.service;

import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.model.ProductionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductionOrderService {
    ProductionOrderResponseDTO createOrder(ProductionOrderRequestDTO dto);
    ProductionOrderResponseDTO getById(Long id);
    Page<ProductionOrderResponseDTO> getAll(Pageable pageable);
    Page<ProductionOrderResponseDTO> getByStatus(Pageable pageable, ProductionStatus status);
    ProductionOrderResponseDTO updateStatus(Long orderId, ProductionStatus status);
    ProductionOrderResponseDTO startProduction(Long orderId);
    void cancelOrder(Long id);
}
