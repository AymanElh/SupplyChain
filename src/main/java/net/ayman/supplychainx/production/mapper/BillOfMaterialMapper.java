package net.ayman.supplychainx.production.mapper;

import net.ayman.supplychainx.production.dto.bom.BillOfMaterialRequestDTO;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialResponseDTO;
import net.ayman.supplychainx.production.model.BillOfMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillOfMaterialMapper {

    @Mapping(target = "materialName", ignore = true)
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitCost", source = "priceAtOrder")
    @Mapping(target = "totalCost", expression = "java(bom.calculateTotalCost())")
    BillOfMaterialResponseDTO toResponseDTO(BillOfMaterial bom);

    @Mapping(target = "materialId", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    BillOfMaterial toEntity(BillOfMaterialRequestDTO bom);
}
