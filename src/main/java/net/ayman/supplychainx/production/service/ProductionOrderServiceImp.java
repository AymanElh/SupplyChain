package net.ayman.supplychainx.production.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.mapper.ProductionOrderMapper;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.model.ProductionOrder;
import net.ayman.supplychainx.production.model.ProductionStatus;
import net.ayman.supplychainx.production.repository.ProductRepository;
import net.ayman.supplychainx.production.repository.ProductionOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ProductionOrderServiceImp implements ProductionOrderService {

    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;

    public ProductionOrderServiceImp(ProductionOrderMapper productionOrderMapper, ProductionOrderRepository productionOrderRepository, ProductRepository productRepository) {
        this.productionOrderMapper = productionOrderMapper;
        this.productionOrderRepository = productionOrderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductionOrderResponseDTO createOrder(ProductionOrderRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with this id " + dto.getProductId() + " not found"));
        ProductionOrder order = productionOrderMapper.toEntity(dto);
        order.setProduct(product);
        log.info("Mapped order dto to entity: {}", order);
        ProductionOrder savedOrder = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public ProductionOrderResponseDTO updateStatus(Long orderId, ProductionStatus status) {
        ProductionOrder order = productionOrderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        if (order.getStatus() == ProductionStatus.FINISHED) {
            throw new BusinessRuleException("Cannot update status of a finished order");
        }
        order.setStatus(status);
        ProductionOrder updatedOrder = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    public ProductionOrderResponseDTO getById(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        return productionOrderMapper.toResponseDTO(order);
    }

    @Override
    public Page<ProductionOrderResponseDTO> getAll(Pageable pageable) {
        return productionOrderRepository.findAll(pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    @Override
    public Page<ProductionOrderResponseDTO> getByStatus(Pageable pageable, ProductionStatus status) {
        return productionOrderRepository.findByStatus(status, pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    @Override
    public ProductionOrderResponseDTO startProduction(Long orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.canBeStart()) {
            throw new BusinessRuleException("you can't start a order with status " + order.getStatus());
        }

        order.startProduction();
        ProductionOrder updatedOrder = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    public ProductionOrderResponseDTO completeProduction(Long orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.canBeCompleted()) {
            throw new BusinessRuleException("You can't complete order with this status " + order.getStatus());
        }
        order.completeOrder();
        Product product = order.getProduct();
        product.updateStock(order.getQuantity());
        productRepository.save(product);
        ProductionOrder updated = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDTO(updated);
    }

    @Override
    public void cancelOrder(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));

        if (!order.canBeCancelled()) {
            throw new BusinessRuleException("You can't cancel a production order with status " + order.getStatus());
        }

        productionOrderRepository.delete(order);
    }
}
