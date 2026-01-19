package net.ayman.supplychainx.supply.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialRequest;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import net.ayman.supplychainx.supply.service.RawMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/materials")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @GetMapping("/me")
    public ResponseEntity<Authentication> me(Authentication auth) {
        return ResponseEntity.ok(auth);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<RawMaterialResponse>> getAllMaterials(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> getMaterialById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PostMapping
    public ResponseEntity<RawMaterialResponse> createMaterial(@Valid @RequestBody RawMaterialRequest materialDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialService.createNewMaterial(materialDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> updateMaterial(@PathVariable Long id, @Valid @RequestBody RawMaterialRequest materialRequest) {
        return ResponseEntity.ok(rawMaterialService.updateMaterial(id, materialRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable("id") Long id) {
        rawMaterialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
