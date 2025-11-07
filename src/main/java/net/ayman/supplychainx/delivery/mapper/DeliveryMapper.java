package net.ayman.supplychainx.delivery.mapper;

import net.ayman.supplychainx.delivery.dto.delivery.DeliveryResponseDTO;
import net.ayman.supplychainx.delivery.model.CustomerOrder;
import net.ayman.supplychainx.delivery.model.Delivery;
import net.ayman.supplychainx.delivery.model.Driver;
import net.ayman.supplychainx.delivery.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "order", source = "order", qualifiedByName = "mapOrderInfo")
    @Mapping(target = "driver", source = "driver", qualifiedByName = "mapDriverInfo")
    @Mapping(target = "vehicle", source = "vehicle", qualifiedByName = "mapVehicleInfo")
    DeliveryResponseDTO toResponseDTO(Delivery delivery);

    Delivery toEntity(DeliveryResponseDTO deliveryResponseDTO);

    @Named("mapOrderInfo")
    default DeliveryResponseDTO.OrderInfo mapOrderInfo(CustomerOrder order) {
        if (order == null) return null;
        return new DeliveryResponseDTO.OrderInfo(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getName() : null,
                order.getTotalAmount()
        );
    }

    @Named("mapDriverInfo")
    default DeliveryResponseDTO.DriverInfo mapDriverInfo(Driver driver) {
        if (driver == null) return null;
        return new DeliveryResponseDTO.DriverInfo(
                driver.getId(),
                driver.getName(),
                driver.getPhone()
        );
    }

    @Named("mapVehicleInfo")
    default DeliveryResponseDTO.VehicleInfo mapVehicleInfo(Vehicle vehicle) {
        if (vehicle == null) return null;
        return new DeliveryResponseDTO.VehicleInfo(
                vehicle.getId(),
                vehicle.getLicensePlate()
        );
    }
}
