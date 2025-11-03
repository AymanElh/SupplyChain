package net.ayman.supplychainx.supply.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import net.ayman.supplychainx.supply.service.SupplierService;
import net.ayman.supplychainx.validation.OnCreate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<Page<SupplierResponseDTO>> getAllSuppliers(
            int page,
            int size,
            String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(supplierService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<SupplierResponseDTO> searchByName(@RequestParam("name") String name) {
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@Validated(OnCreate.class) @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable("id") Long id, @Valid @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.updateSupplier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable("id") Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

}
