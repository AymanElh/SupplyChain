package net.ayman.supplychainx.delivery.mapper;

import net.ayman.supplychainx.delivery.dto.driver.DriverRequestDTO;
import net.ayman.supplychainx.delivery.dto.driver.DriverResponseDTO;
import net.ayman.supplychainx.delivery.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    Driver toEntity(DriverRequestDTO driverRequestDTO);
    DriverResponseDTO toResponseDTO(Driver driver);

    void updateDriver(DriverRequestDTO driverRequestDTO, @MappingTarget Driver driver);
}
