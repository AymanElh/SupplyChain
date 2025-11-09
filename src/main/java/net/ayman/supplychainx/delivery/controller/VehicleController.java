package net.ayman.supplychainx.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleRequestDTO;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleResponseDTO;
import net.ayman.supplychainx.delivery.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // Vehicle management - SUPERVISEUR_LIVRAISONS
    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        VehicleResponseDTO createdVehicle = vehicleService.createVehicle(vehicleRequestDTO);
        return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable("id") Long id) {
        VehicleResponseDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @GetMapping
    public ResponseEntity<Page<VehicleResponseDTO>> getAllVehicles(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<VehicleResponseDTO> vehicles = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(vehicles);
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDTO vehicleRequestDTO) {
        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleRequestDTO);
        return ResponseEntity.ok(updatedVehicle);
    }

    @RequiredRole({"SUPERVISEUR_LIVRAISONS"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
