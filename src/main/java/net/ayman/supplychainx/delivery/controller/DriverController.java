package net.ayman.supplychainx.delivery.controller;

import jakarta.validation.Valid;
import net.ayman.supplychainx.delivery.dto.driver.DriverRequestDTO;
import net.ayman.supplychainx.delivery.dto.driver.DriverResponseDTO;
import net.ayman.supplychainx.delivery.service.DriverService;
import net.ayman.supplychainx.validation.OnCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> addNewDriver(@Validated(OnCreate.class) @RequestBody DriverRequestDTO driverRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.addNewDriver(driverRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriverInfo(@PathVariable("id") Long id, @Valid @RequestBody DriverRequestDTO driverRequestDTO) {
        return ResponseEntity.ok(driverService.updateDriverInfo(id, driverRequestDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(Long id) {
        driverService.deleteDriver(id);
    }
}
