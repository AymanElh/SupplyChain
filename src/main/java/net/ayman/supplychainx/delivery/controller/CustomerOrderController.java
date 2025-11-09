package net.ayman.supplychainx.delivery.controller;

import jakarta.validation.Valid;
import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;
import net.ayman.supplychainx.delivery.dto.order.UpdateCustomerOrderDTO;
import net.ayman.supplychainx.delivery.model.OrderStatus;
import net.ayman.supplychainx.delivery.service.CustomerOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer-orders")
public class CustomerOrderController {


    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    // US35: Create customer order - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@Valid @RequestBody CustomerOrderRequestDTO dto, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerOrderService.createOrder(dto));
    }

    // US36: Modify customer order - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrderStatus(@PathVariable("id") Long orderId, @RequestBody UpdateCustomerOrderDTO dto) {
        return ResponseEntity.ok(customerOrderService.updateStatus(orderId, dto.status()));
    }

    // US38: View all customer orders - SUPERVISEUR_LIVRAISONS
    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping
    public ResponseEntity<Page<CustomerOrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(customerOrderService.getAllOrders(pageable));
    }


    // US39: Track customer order status - SUPERVISEUR_LIVRAISONS
    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerOrderService.getOrderById(id));
    }

    @RequiredRole({"GESTIONNAIRE_COMMERCIAL", "SUPERVISEUR_LIVRAISONS"})
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<CustomerOrderResponseDTO>> getByCustomer(@PathVariable("id") Long customerId) {
        return ResponseEntity.ok(customerOrderService.getOrdersByCustomerId(customerId));
    }

    // US37: Cancel customer order if not shipped - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
        customerOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
