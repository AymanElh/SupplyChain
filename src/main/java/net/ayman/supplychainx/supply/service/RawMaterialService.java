package net.ayman.supplychainx.supply.service;

import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialRequest;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RawMaterialService {
    RawMaterialResponse createNewMaterial(RawMaterialRequest materialDto);
    RawMaterialResponse updateMaterial(Long id, RawMaterialRequest materialDto);
    void deleteMaterial(Long id);
    RawMaterialResponse getById(Long id);
    Page<RawMaterialResponse> getAll(Pageable pageable);
}
