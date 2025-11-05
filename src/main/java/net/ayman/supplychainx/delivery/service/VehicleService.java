package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.delivery.dto.vehicle.VehicleRequestDTO;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponseDTO createVehicle(VehicleRequestDTO vehicleRequestDTO);
    VehicleResponseDTO getVehicleById(Long id);
    Page<VehicleResponseDTO> getAllVehicles(Pageable pageable);
    VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO vehicleRequestDTO);
    void deleteVehicle(Long id);
}