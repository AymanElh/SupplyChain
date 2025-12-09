package net.ayman.supplychainx.supply.api;

import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;

public interface SupplyFacade {
    RawMaterialResponse getMaterialById(Long materialId);
    boolean hasAvailableStock(Long materialId, Integer quantity);
    void reduceStock(Long materialId, Integer quantity);
}
