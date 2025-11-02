package net.ayman.supplychainx.production.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.DuplicateResourceException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialRequestDTO;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialResponseDTO;
import net.ayman.supplychainx.production.mapper.BillOfMaterialMapper;
import net.ayman.supplychainx.production.model.BillOfMaterial;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.BillOfMaterialRepository;
import net.ayman.supplychainx.production.repository.ProductRepository;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BillOfMaterialServiceImp implements BillOfMaterialService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final BillOfMaterialMapper billOfMaterialMapper;
    private final BillOfMaterialRepository billOfMaterialRepository;

    public BillOfMaterialServiceImp(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository, BillOfMaterialMapper billOfMaterialMapper, BillOfMaterialRepository billOfMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.billOfMaterialMapper = billOfMaterialMapper;
        this.billOfMaterialRepository = billOfMaterialRepository;
    }

    @Override
    public BillOfMaterialResponseDTO addMaterialToProduct(Long productId, BillOfMaterialRequestDTO bomDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));

        RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId()).orElseThrow(() -> new ResourceNotFoundException("Material with this id " + bomDTO.getMaterialId() + " not found"));

        if (billOfMaterialRepository.existsByProductIdAndMaterialId(productId, bomDTO.getMaterialId())) {
            throw new DuplicateResourceException("This bill of material is already exist");
        }

//        BillOfMaterial bom  = billOfMaterialMapper.toEntity(bomDTO);
        BillOfMaterial bom = new BillOfMaterial();
        bom.setProduct(product);
        bom.setMaterial(material);
        bom.setQuantity(bomDTO.getQuantity());
        log.info("Mapping bom from dto to entity: {} to {}", bomDTO, bom);
        BillOfMaterial savedBom = billOfMaterialRepository.save(bom);
        return billOfMaterialMapper.toResponseDTO(bom);
    }

    @Override
    public List<BillOfMaterialResponseDTO> getProductBill(Long productId) {
        return billOfMaterialRepository.findByProductId(productId)
                .stream()
                .map(billOfMaterialMapper::toResponseDTO)
                .toList();
    }

    @Override
    public BillOfMaterialResponseDTO updateQuantity(Long bomId, Integer quantity) {
        BillOfMaterial bill = billOfMaterialRepository.findById(bomId).orElseThrow(() -> new ResourceNotFoundException("Bill of material with id " + bomId + " not found"));
        bill.setQuantity(quantity);
        BillOfMaterial updatedBom = billOfMaterialRepository.save(bill);
        return billOfMaterialMapper.toResponseDTO(updatedBom);
    }

    @Override
    public void removeMaterial(Long bomId) {
        BillOfMaterial bom = billOfMaterialRepository.findById(bomId).orElseThrow(() -> new ResourceNotFoundException("Bill of material with this id " + bomId + " not found"));
        billOfMaterialRepository.delete(bom);
    }
}
