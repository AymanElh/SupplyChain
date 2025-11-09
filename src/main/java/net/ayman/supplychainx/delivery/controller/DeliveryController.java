package net.ayman.supplychainx.delivery.controller;

import jakarta.validation.Valid;
import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryRequestDTO;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryResponseDTO;
import net.ayman.supplychainx.delivery.dto.delivery.UpdateDeliveryStatus;
import net.ayman.supplychainx.delivery.model.OrderStatus;
import net.ayman.supplychainx.delivery.service.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Delivery management - SUPERVISEUR_LIVRAISONS
    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(deliveryService.getAllDeliveries(pageable));
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<DeliveryResponseDTO>> getByCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @PathVariable("id") Long customerId
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(deliveryService.getDeliveriesByCustomerId(customerId, pageable));
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@Valid @RequestBody DeliveryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(dto));
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UpdateDeliveryStatus dto) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, dto.status()));
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
