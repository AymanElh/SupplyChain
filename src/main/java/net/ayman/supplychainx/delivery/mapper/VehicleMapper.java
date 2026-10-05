package net.ayman.supplychainx.delivery.mapper;

import net.ayman.supplychainx.delivery.dto.vehicle.VehicleRequestDTO;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleResponseDTO;
import net.ayman.supplychainx.delivery.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequestDTO vehicleRequestDTO) {
        if (vehicleRequestDTO == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(vehicleRequestDTO.licensePlate());
        vehicle.setType(vehicleRequestDTO.type());
        vehicle.setModel(vehicleRequestDTO.model());
        return vehicle;
    }

    public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getModel()
        );
    }

    public void updateEntityFromDto(VehicleRequestDTO dto, Vehicle entity) {
        if (dto == null) {
            return;
        }
        if (dto.licensePlate() != null) {
            entity.setLicensePlate(dto.licensePlate());
        }
        if (dto.type() != null) {
            entity.setType(dto.type());
        }
        if (dto.model() != null) {
            entity.setModel(dto.model());
        }
    }
}
