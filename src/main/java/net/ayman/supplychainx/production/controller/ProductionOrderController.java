package net.ayman.supplychainx.production.controller;

import jakarta.validation.Valid;
import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.dto.order.UpdateProductionOrderStatusDTO;
import net.ayman.supplychainx.production.dto.product.UpdateProductQuantityDTO;
import net.ayman.supplychainx.production.model.ProductionOrder;
import net.ayman.supplychainx.production.model.ProductionStatus;
import net.ayman.supplychainx.production.service.ProductionOrderService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production-orders")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    ProductionOrderController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    @RequiredRole({"CHEF_PRODUCTION"})
    @PostMapping
    public ResponseEntity<ProductionOrderResponseDTO> createNewOrder(@Valid @RequestBody ProductionOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionOrderService.createOrder(dto));
    }

    @RequiredRole({"SUPERVISEUR_PRODUCTION"})
    @GetMapping
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(productionOrderService.getAll(pageable));
    }

    @RequiredRole({"SUPERVISEUR_PRODUCTION"})
    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> getOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productionOrderService.getById(id));
    }

    @RequiredRole({"SUPERVISEUR_PRODUCTION"})
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getByStatus(
            @PathVariable("status") ProductionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(productionOrderService.getByStatus(pageable, status));
    }

    @RequiredRole({"CHEF_PRODUCTION"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductionOrderResponseDTO> updateStatus(@PathVariable("id") Long orderId, @Valid @RequestBody UpdateProductionOrderStatusDTO dto) {
        return ResponseEntity.ok(productionOrderService.updateStatus(orderId, dto.getStatus()));
    }

    @RequiredRole({"PLANIFICATEUR"})
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ProductionOrderResponseDTO> updateQuantity(@PathVariable("id") Long orderId, @Valid @RequestBody UpdateProductQuantityDTO dto) {
        return ResponseEntity.ok(productionOrderService.updateQuantity(orderId, dto.quantity()));
    }

    @RequiredRole({"PLANIFICATEUR"})
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    @PostMapping("/{id}/start-production")
    public ResponseEntity<ProductionOrderResponseDTO> startProd(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productionOrderService.startProduction(id));
    }

    @PostMapping("/{id}/complete-production")
    public ResponseEntity<ProductionOrderResponseDTO> completeProd(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productionOrderService.completeProduction(id));
    }

    @RequiredRole({"CHEF_PRODUCTION"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") Long id) {
        productionOrderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
