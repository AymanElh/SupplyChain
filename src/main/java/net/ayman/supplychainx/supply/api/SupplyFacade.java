package net.ayman.supplychainx.supply.api;

import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;

import java.util.List;
import java.util.Set;

public interface SupplyFacade {
    RawMaterialResponse getMaterialById(Long materialId);
    List<RawMaterialResponse> getMaterialsByIds(Set<Long> materialIds);
    boolean hasAvailableStock(Long materialId, Integer quantity);
    void reduceStock(Long materialId, Integer quantity);
}
