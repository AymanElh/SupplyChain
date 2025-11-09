package net.ayman.supplychainx.delivery.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.delivery.dto.address.AddressRequestDTO;
import net.ayman.supplychainx.delivery.dto.address.AddressResponseDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // US30: Add client - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> addCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addCustomer(customerRequestDTO));
    }

    // US33: View all clients - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @PostMapping("/{id}/addresses")
    public ResponseEntity<CustomerResponseDTO> addAddressesToCustomer(@PathVariable Long id, @Valid @RequestBody AddressRequestDTO address) {
        return ResponseEntity.ok(customerService.addAddressesToCustomer(id, address));
    }

    // US34: Search client by name - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // US31: Modify client - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomerInfo(@PathVariable("id") Long id, @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        return ResponseEntity.ok(customerService.editCustomerInfo(id, customerRequestDTO));
    }

    // US32: Delete client if no active order - GESTIONNAIRE_COMMERCIAL
    @RequiredRole({"GESTIONNAIRE_COMMERCIAL"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCustomer(@PathVariable("id") Long id) {
        log.debug("Deleting customer with id {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
