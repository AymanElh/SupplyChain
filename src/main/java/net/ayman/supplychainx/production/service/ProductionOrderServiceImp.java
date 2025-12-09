package net.ayman.supplychainx.production.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.mapper.ProductionOrderMapper;
import net.ayman.supplychainx.production.model.BillOfMaterial;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.model.ProductionOrder;
import net.ayman.supplychainx.production.model.ProductionStatus;
import net.ayman.supplychainx.production.repository.BillOfMaterialRepository;
import net.ayman.supplychainx.production.repository.ProductRepository;
import net.ayman.supplychainx.production.repository.ProductionOrderRepository;
import net.ayman.supplychainx.supply.api.SupplyFacade;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
public class ProductionOrderServiceImp implements ProductionOrderService {

    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final SupplyFacade supplyFacade;

    public ProductionOrderServiceImp(ProductionOrderMapper productionOrderMapper, ProductionOrderRepository productionOrderRepository, ProductRepository productRepository, BillOfMaterialRepository billOfMaterialRepository, SupplyFacade supplyFacade) {
        this.productionOrderMapper = productionOrderMapper;
        this.productionOrderRepository = productionOrderRepository;
        this.productRepository = productRepository;
        this.billOfMaterialRepository = billOfMaterialRepository;
        this.supplyFacade = supplyFacade;
    }

    @Override
    @Transactional
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
    @Transactional
    public ProductionOrderResponseDTO updateQuantity(Long orderId, Integer quantity) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        if (order.getStatus() == ProductionStatus.IN_WAITING) {
            order.setQuantity(quantity);
        }
        return productionOrderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO startProduction(Long orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.canBeStart()) {
            throw new BusinessRuleException("you can't start a order with status " + order.getStatus());
        }

        Product product = order.getProduct();
        List<BillOfMaterial> bills = billOfMaterialRepository.findByProductId(product.getId());

        for (BillOfMaterial bom : bills) {
            int requiredQuantity = bom.getQuantity() * order.getQuantity();
            RawMaterialResponse material = supplyFacade.getMaterialById(bom.getMaterialId());
            
            if (!supplyFacade.hasAvailableStock(bom.getMaterialId(), requiredQuantity)) {
                throw new BusinessRuleException("Not enough stock for material: " + material.getName());
            }
        }
        
        for (BillOfMaterial bom : bills) {
            int requiredQuantity = bom.getQuantity() * order.getQuantity();
            supplyFacade.reduceStock(bom.getMaterialId(), requiredQuantity);
        }

        order.startProduction();

        // Calculate end date
        int totalProductionHours = product.getProductionTime() * order.getQuantity();
        int productionDays = (int) Math.ceil((double) totalProductionHours / 8.0); // Assuming 8-hour work day
        order.setEndDate(order.getStartDate().plusDays(productionDays));

        ProductionOrder updatedOrder = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    @Transactional
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
    @Transactional
    public void cancelOrder(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));

        if (!order.canBeCancelled()) {
            throw new BusinessRuleException("You can't cancel a production order with status " + order.getStatus());
        }

        productionOrderRepository.delete(order);
    }
}
