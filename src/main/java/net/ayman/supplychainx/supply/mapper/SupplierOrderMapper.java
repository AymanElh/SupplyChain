package net.ayman.supplychainx.supply.mapper;

import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.model.SupplierOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierOrderMapper {
    SupplierOrderResponseDTO toResponseDTO(SupplierOrder order);
}
