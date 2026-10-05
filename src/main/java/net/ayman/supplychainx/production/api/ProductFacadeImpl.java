package net.ayman.supplychainx.production.api;

import lombok.RequiredArgsConstructor;
import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;
import net.ayman.supplychainx.production.mapper.ProductMapper;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductFacadeImpl implements ProductFacade {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %d not found", productId)));


        return productMapper.toResponseDTO(product);
    }

    @Override
    public boolean hasAvailableStock(Long productId, Integer requestedQuantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + productId + " not found"));

        return product.getStock() >= requestedQuantity;
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + productId + " not found"));

        if (product.getStock() < quantity) {
            throw new BusinessRuleException(
                    String.format("Product '%s' has insufficient stock. Available: %d, Requested: %d",
                            product.getName(), product.getStock(), quantity));
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void releaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + productId + " not found"));


        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}
