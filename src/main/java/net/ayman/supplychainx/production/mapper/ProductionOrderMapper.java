package net.ayman.supplychainx.production.mapper;

import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.model.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ProductionOrderResponseDTO toResponseDTO(ProductionOrder order);

    @Mapping(target = "product", ignore = true)
    ProductionOrder toEntity(ProductionOrderRequestDTO dto);
}
