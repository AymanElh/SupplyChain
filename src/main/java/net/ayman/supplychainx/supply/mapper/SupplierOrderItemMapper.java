package net.ayman.supplychainx.supply.mapper;

import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemResponseDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.model.SupplierOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierOrderItemMapper {

    @Mapping(source = "rawMaterial.id", target = "materialId")
    @Mapping(source = "rawMaterial.name", target = "materialName")
    @Mapping(source = "subTotal", target = "subTotal")
    SupplierOrderItemResponseDTO toResponseDTO(SupplierOrderItem item);

    @Mapping(source = "materialId", target = "rawMaterial.id")
    SupplierOrderItem toEntity(SupplierOrderItemRequestDTO orderRequest);
}
