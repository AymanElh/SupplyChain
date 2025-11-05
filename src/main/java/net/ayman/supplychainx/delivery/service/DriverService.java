package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.delivery.dto.driver.DriverRequestDTO;
import net.ayman.supplychainx.delivery.dto.driver.DriverResponseDTO;
import net.ayman.supplychainx.delivery.model.Driver;

import java.util.List;

public interface DriverService {
    DriverResponseDTO addNewDriver(DriverRequestDTO driverRequestDTO);
    DriverResponseDTO updateDriverInfo(Long id, DriverRequestDTO driverRequestDTO);
    void deleteDriver(Long id);
    List<DriverResponseDTO> getAllDrivers();
    DriverResponseDTO getDriverById(Long id);
}
