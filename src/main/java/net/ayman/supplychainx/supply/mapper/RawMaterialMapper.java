package net.ayman.supplychainx.supply.mapper;

import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialRequest;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import net.ayman.supplychainx.supply.model.RawMaterial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterialResponse toResponseDTO(RawMaterial rawMaterial);
    RawMaterial toEntity(RawMaterialRequest rawMaterialRequest);
}
