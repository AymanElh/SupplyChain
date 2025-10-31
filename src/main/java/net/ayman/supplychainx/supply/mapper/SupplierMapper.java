package net.ayman.supplychainx.supply.mapper;

import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import net.ayman.supplychainx.supply.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponseDTO toResponseDTO(Supplier supplier);
    Supplier toEntity(SupplierRequestDTO dto);

    List<SupplierResponseDTO> toResponseDTOList(List<Supplier> suppliers);
}
