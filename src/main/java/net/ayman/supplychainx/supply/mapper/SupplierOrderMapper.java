package net.ayman.supplychainx.supply.mapper;

import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.model.Supplier;
import net.ayman.supplychainx.supply.model.SupplierOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SupplierOrderItemMapper.class})
public interface SupplierOrderMapper {

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "items", source = "items")
    SupplierOrderResponseDTO toResponseDTO(SupplierOrder order);


    @Mapping(target = "orderDate", source = "orderDate")
    @Mapping(source = "items", target = "items")
    @Mapping(target = "supplier", ignore = true)
    SupplierOrder toEntity(SupplierOrderRequestDTO orderRequest);

}
