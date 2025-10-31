package net.ayman.supplychainx.supply.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.mapper.SupplierOrderItemMapper;
import net.ayman.supplychainx.supply.mapper.SupplierOrderMapper;
import net.ayman.supplychainx.supply.model.*;
import net.ayman.supplychainx.supply.repository.SupplierOrderRepository;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierOrderServiceImp implements SupplierOrderService {

    private final SupplierOrderMapper supplierOrderMapper;
    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierOrderItemMapper supplierOrderItemMapper;
    private final SupplierRepository supplierRepository;

    public SupplierOrderServiceImp(SupplierOrderMapper supplierOrderMapper, SupplierOrderRepository supplierOrderRepository, SupplierOrderItemMapper supplierOrderItemMapper, SupplierRepository supplierRepository) {
        this.supplierOrderMapper = supplierOrderMapper;
        this.supplierOrderRepository = supplierOrderRepository;
        this.supplierOrderItemMapper = supplierOrderItemMapper;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Page<SupplierOrderResponseDTO> getAll(Pageable pageable) {
        return supplierOrderRepository.findAll(pageable)
                .map(supplierOrderMapper::toResponseDTO);
    }

    @Override
    public SupplierOrderResponseDTO getById(Long id) {
        log.info("Getting supplier by id: {}", id);
        SupplierOrder order = supplierOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("This order with this id " + id + " not exist"));
        log.debug("Gotted supplier from database: {}", order);
        return supplierOrderMapper.toResponseDTO(order);
    }

    @Override
    public SupplierOrderResponseDTO createOrder(SupplierOrderRequestDTO dto) {
        log.info("Creating supply order for supplier Id {}", dto.getSupplierId());
        SupplierOrder order = supplierOrderMapper.toEntity(dto);

        Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier with this id " + dto.getSupplierId() + " not found"));

        List<SupplierOrderItem> items = dto.getItems().stream()
                .map(supplierOrderItemMapper::toEntity)
                .toList();

        for (SupplierOrderItem item: items) {
            if(!supplier.getMaterials().contains(item.getRawMaterial())) {
                throw new IllegalArgumentException("This material " + item.getRawMaterial().getName() + " not exist on that supplier");
            }
        }

        // check if the target supplier has the materials on the order
        System.out.println("Supplier in the order: " + order.getSupplier().getMaterials());

        items.forEach(item -> {
            item.setOrder(order);
            item.calculateSubTotal();
        });

        order.setItems(items);


        double totalAmount = items.stream()
                .mapToDouble(item -> item.getSubTotal() != null ? item.getSubTotal() : 0.0)
                .sum();
        order.setAmount(totalAmount);

        SupplierOrder savedOrder = supplierOrderRepository.save(order);
        log.info("Supply order created successfully with ID: {}", savedOrder.getId());
        return supplierOrderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public SupplierOrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        log.info("Updating status for order Id: {} to {}", id, status);
        SupplierOrder order = supplierOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.RECEIVED) {
            log.warn("Attemped to change status of received order");
            throw new IllegalArgumentException("Change status of received order");
        }

        order.setStatus(status);
        SupplierOrder updatedOrder = supplierOrderRepository.save(order);
        log.info("Order status updated successfully {} -> {}", order.getStatus(), status);
        return supplierOrderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    public SupplierOrderResponseDTO receiveOrder(Long id) {
        SupplierOrder order = supplierOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("This order with id " + id + " not found"));
        if (order.getStatus() == OrderStatus.RECEIVED) {
            throw new IllegalArgumentException("Order already received");
        }

        for (SupplierOrderItem item: order.getItems()) {
            RawMaterial material = item.getRawMaterial();
            int oldStock = material.getStock();
            material.updateStock(item.getQuantity());
        }

        order.markReceived();
        return supplierOrderMapper.toResponseDTO(supplierOrderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        SupplierOrder order = supplierOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(!order.canBeDeleted()) {
            throw new IllegalArgumentException("Cannot delete received order");
        }

        order.softDelete();
        supplierOrderRepository.save(order);
    }
}
