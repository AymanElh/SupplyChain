package net.ayman.supplychainx.delivery.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.driver.DriverRequestDTO;
import net.ayman.supplychainx.delivery.dto.driver.DriverResponseDTO;
import net.ayman.supplychainx.delivery.mapper.DriverMapper;
import net.ayman.supplychainx.delivery.model.Driver;
import net.ayman.supplychainx.delivery.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DriverServiceImp implements DriverService {


    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;

    public DriverServiceImp(DriverMapper driverMapper, DriverRepository driverRepository) {
        this.driverMapper = driverMapper;
        this.driverRepository = driverRepository;
    }

    @Override
    public DriverResponseDTO addNewDriver(DriverRequestDTO driverRequestDTO) {
        log.debug("Adding new driver: {}", driverRequestDTO);
        Driver driver = driverMapper.toEntity(driverRequestDTO);
        log.debug("Driver to be saved: {}", driver);
        return driverMapper.toResponseDTO(driverRepository.save(driver));
    }

    @Override
    public DriverResponseDTO updateDriverInfo(Long id, DriverRequestDTO driverRequestDTO) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found"));
        driverMapper.updateDriver(driverRequestDTO, driver);
        driverRepository.save(driver);
        return driverMapper.toResponseDTO(driver);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found"));
        driverRepository.delete(driver);
    }

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(driverMapper::toResponseDTO)
                .toList();
    }

    @Override
    public DriverResponseDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found"));
        return driverMapper.toResponseDTO(driver);
    }
}
