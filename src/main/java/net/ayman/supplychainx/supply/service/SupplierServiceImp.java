package net.ayman.supplychainx.supply.service;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import net.ayman.supplychainx.supply.mapper.SupplierMapper;
import net.ayman.supplychainx.supply.model.Supplier;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SupplierServiceImp implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierServiceImp(SupplierMapper supplierMapper, SupplierRepository supplierRepository) {
        this.supplierMapper = supplierMapper;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierResponseDTO createSupplier(SupplierRequestDTO dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        return supplierMapper.toResponseDTO(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with this id not found " + id));
        if (dto.getName() != null) {
            supplier.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            supplier.setPhone(dto.getPhone());
        }
        if (dto.getLeadTime() != null) {
            supplier.setLeadTime(dto.getLeadTime());
        }
        if(dto.getRating() != null) {
            supplier.setRating(dto.getRating());
        }
        if (dto.getEmail() != null) {
            supplier.setEmail(dto.getEmail());
        }

        return supplierMapper.toResponseDTO(supplierRepository.save(supplier));
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with this id not found " + id));
        supplierRepository.delete(supplier);
    }

    @Override
    public Page<SupplierResponseDTO> getAll(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(supplierMapper::toResponseDTO);
    }

    @Override
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with this id " + id + " not found"));
        return supplierMapper.toResponseDTO(supplier);
    }

    @Override
    public SupplierResponseDTO searchByName(String name) {
        Supplier supp = supplierRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Supplier with name " + name + " not exit"));
        return supplierMapper.toResponseDTO(supp);
    }
}
