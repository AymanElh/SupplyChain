package net.ayman.supplychainx.production.controller;

import jakarta.validation.Valid;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialRequestDTO;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialResponseDTO;
import net.ayman.supplychainx.production.service.BillOfMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class BillOfMaterialController {

    private final BillOfMaterialService billOfMaterialService;

    public BillOfMaterialController(BillOfMaterialService billOfMaterialService) {
        this.billOfMaterialService = billOfMaterialService;
    }

    @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @PostMapping("/{productId}/bills")
    public ResponseEntity<BillOfMaterialResponseDTO> addMaterial(@PathVariable("productId") Long productId, @Valid @RequestBody BillOfMaterialRequestDTO bomDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billOfMaterialService.addMaterialToProduct(productId, bomDto));
    }

    @PreAuthorize("hasAnyRole('SUPERVISEUR_PRODUCTION', 'PLANIFICATEUR')")
    @GetMapping("/{productId}/bills")
    public ResponseEntity<List<BillOfMaterialResponseDTO>> getProductBills(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(billOfMaterialService.getProductBill(productId));
    }

    @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @PatchMapping("/bill/{bomId}")
    public ResponseEntity<BillOfMaterialResponseDTO> updateQuantity(@PathVariable("bomId") Long bomId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(billOfMaterialService.updateQuantity(bomId, quantity));
    }

    @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @DeleteMapping("/bom/{bomId}")
    public ResponseEntity<Void> removeMaterial(@PathVariable Long bomId) {
        billOfMaterialService.removeMaterial(bomId);
        return ResponseEntity.noContent().build();
    }
}
