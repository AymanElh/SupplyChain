package net.ayman.supplychainx.supply.api;

import lombok.RequiredArgsConstructor;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import net.ayman.supplychainx.supply.mapper.RawMaterialMapper;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplyFacadeImpl implements SupplyFacade{
    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    public RawMaterialResponse getMaterialById(Long materialId) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Raw material with id %d not found", materialId)));

        return rawMaterialMapper.toResponseDTO(material);
    }

    @Override
    public boolean hasAvailableStock(Long materialId, Integer quantity) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Raw material with id %d not found", materialId)));
        return material.getStock() >= quantity;
    }

    @Override
    public void reduceStock(Long materialId, Integer quantity) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Raw material with id %d not found", materialId)));
        
        if (material.getStock() < quantity) {
            throw new IllegalStateException(String.format("Not enough stock for material: %s. Available: %d, Required: %d", 
                material.getName(), material.getStock(), quantity));
        }
        
        material.setStock(material.getStock() - quantity);
        rawMaterialRepository.save(material);
    }
}
