package net.ayman.supplychainx.supply.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.model.SupplierOrder;
import net.ayman.supplychainx.supply.service.SupplierOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/supplier-orders")
public class SupplierOrderController {

    private final SupplierOrderService supplierOrderService;

    public SupplierOrderController(SupplierOrderService supplierOrderService) {
        this.supplierOrderService = supplierOrderService;
    }

    @PostMapping
    public ResponseEntity<SupplierOrderResponseDTO> createOrder(@Valid @RequestBody SupplierOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierOrderService.createOrder(dto));
    }

    @GetMapping
    public ResponseEntity<Page<SupplierOrderResponseDTO>> getAllOrders(
            int page,
            int size,
            String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(supplierOrderService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrderResponseDTO> getOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(supplierOrderService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        supplierOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SupplierOrderResponseDTO> updateOrderStatus(@PathVariable("id") Long id, @Valid @RequestBody SupplierOrderRequestDTO dto) {
        log.info("REST request to update status for order id: {} to {}", id, dto.getStatus());
        return ResponseEntity.ok(supplierOrderService.updateOrderStatus(id, dto.getStatus()));
    }
}
