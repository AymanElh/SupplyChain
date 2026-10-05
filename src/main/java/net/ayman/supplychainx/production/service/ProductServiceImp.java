package net.ayman.supplychainx.production.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.DuplicateResourceException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.product.ProductRequestDTO;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;
import net.ayman.supplychainx.production.mapper.ProductMapper;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImp(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        if (productRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Product with name " + dto.getName() + " already exists");
        }
        log.debug("Product request dto: {}", dto);
        Product product = productMapper.toEntity(dto);
        log.debug("Mapping dto to entity: {}", product);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.canBeDeleted()) {
            throw new IllegalStateException("Cannot delete a product with production orders");
        }
        product.softDelete();
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        log.debug("Products got from database: {}", products);
        return products.map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with this id " + id + " not found"));

        if (productRequest.getName() != null && !product.getName().equals(productRequest.getName()) && productRepository.existsByName(productRequest.getName())) {
            throw new DuplicateResourceException("Product with name " + productRequest.getName() + " already exists");
        }

        productMapper.updateEntityFromDTO(productRequest, product);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }
}
