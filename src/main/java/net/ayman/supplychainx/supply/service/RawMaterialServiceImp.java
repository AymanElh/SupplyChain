package net.ayman.supplychainx.supply.service;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.exception.MaterialAlreadyExistsException;
import net.ayman.supplychainx.supply.exception.MaterialInUseException;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialRequest;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import net.ayman.supplychainx.supply.mapper.RawMaterialMapper;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class RawMaterialServiceImp implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    public RawMaterialServiceImp(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @Override
    public RawMaterialResponse createNewMaterial(RawMaterialRequest materialDto) {
        if (rawMaterialRepository.existsByName(materialDto.getName())) {
            throw new MaterialAlreadyExistsException("Material with this name is already exist");
        }

        RawMaterial material = rawMaterialMapper.toEntity(materialDto);
        return rawMaterialMapper.toResponseDTO(rawMaterialRepository.save(material));
    }

    @Override
    public RawMaterialResponse updateMaterial(Long id, RawMaterialRequest materialRequest) {
        RawMaterial material = rawMaterialRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Material with id " + id + " not found"));
        if (materialRequest.getName() != null && !material.getName().equals(materialRequest.getName()) && rawMaterialRepository.existsByName(materialRequest.getName())) {
            throw new MaterialAlreadyExistsException("Material with name " + materialRequest.getName());
        }
        return null;
    }

    @Override
    public void deleteMaterial(Long id) {
        RawMaterial material = rawMaterialRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Material with this id " + id + " not found"));
        if (material.isMaterialInOrdering()) {
            throw new MaterialInUseException("Cannot delete material " + material.getName() + " because is used on orders");
        }
        material.softDelete();
        rawMaterialRepository.save(material);
    }

    @Override
    public RawMaterialResponse getById(Long id) {
        RawMaterial material = rawMaterialRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Material with this id " + id + " not found"));
        return rawMaterialMapper.toResponseDTO(material);
    }

    @Override
    public Page<RawMaterialResponse> getAll(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }
}
