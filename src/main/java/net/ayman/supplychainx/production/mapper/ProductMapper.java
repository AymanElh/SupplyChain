package net.ayman.supplychainx.production.mapper;

import net.ayman.supplychainx.production.dto.product.ProductRequestDTO;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;
import net.ayman.supplychainx.production.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Product toEntity(ProductRequestDTO productRequest);

    // TODO: I will add the expressions of response dto that I get from entity methods
    @Mapping(target = "materialCost", expression = "java(product.calculateMaterialCost())")
    @Mapping(target = "profitMargin", expression = "java(product.calculateProfitMargin())")
    @Mapping(target = "hasBom", expression = "java(product.hasBom())")
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(ProductRequestDTO dto, @MappingTarget Product product);
}
