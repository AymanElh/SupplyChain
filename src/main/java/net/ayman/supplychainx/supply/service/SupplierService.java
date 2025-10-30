package net.ayman.supplychainx.supply.service;

import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {
    SupplierResponseDTO createSupplier(SupplierRequestDTO dto);
    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO dto);
    void deleteSupplier(Long id);
    Page<SupplierResponseDTO> getAll(Pageable pageable);
    SupplierResponseDTO getSupplierById(Long id);
    SupplierResponseDTO searchByName(String name);
}
