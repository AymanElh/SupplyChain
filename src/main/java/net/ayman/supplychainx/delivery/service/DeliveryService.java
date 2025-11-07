package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.delivery.dto.delivery.DeliveryRequestDTO;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryService {
    DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryRequestDTO);
    void deleteDelivery(Long id);
    DeliveryResponseDTO getDeliveryById(Long id);
    Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable);
    DeliveryResponseDTO updateDeliveryStatus(Long id, String status);
    Page<DeliveryResponseDTO> getDeliveriesByCustomerId(Long customerId, Pageable pageable);
}
