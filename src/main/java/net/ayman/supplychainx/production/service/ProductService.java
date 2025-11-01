package net.ayman.supplychainx.production.service;

import net.ayman.supplychainx.production.dto.product.ProductRequestDTO;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO dto);
    void deleteProduct(Long id);
    Page<ProductResponseDTO> getAll(Pageable pageable);
    ProductResponseDTO getById(Long id);
    ProductResponseDTO update(Long id, ProductRequestDTO productRequest);
}
