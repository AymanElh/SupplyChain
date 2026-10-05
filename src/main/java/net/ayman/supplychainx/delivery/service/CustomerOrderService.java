package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.delivery.dto.order.CustomerOrderRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;
import net.ayman.supplychainx.delivery.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerOrderService {
    CustomerOrderResponseDTO createOrder(CustomerOrderRequestDTO dto);
    CustomerOrderResponseDTO getOrderById(Long id);
    void deleteOrder(Long id);
    Page<CustomerOrderResponseDTO> getAllOrders(Pageable pageable);
    Page<CustomerOrderResponseDTO> getAllOrdersByStatus(OrderStatus status, Pageable pageable);
    List<CustomerOrderResponseDTO> getOrdersByCustomerId(Long customerId);
    CustomerOrderResponseDTO updateStatus(Long orderId, OrderStatus status);
    CustomerOrderResponseDTO updateQuantity(Long orderId, Long quantity);
}
