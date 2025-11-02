package net.ayman.supplychainx.production.service;

import net.ayman.supplychainx.production.dto.bom.BillOfMaterialRequestDTO;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialResponseDTO;

import java.util.List;

public interface BillOfMaterialService {
    BillOfMaterialResponseDTO addMaterialToProduct(Long productId, BillOfMaterialRequestDTO bomDTO);
    List<BillOfMaterialResponseDTO> getProductBill(Long productId);
    BillOfMaterialResponseDTO updateQuantity(Long bomId, Integer quantity);
    void removeMaterial(Long bomId);
}
