package net.ayman.supplychainx.production.api;

import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;

public interface ProductFacade {
    ProductResponseDTO getProductById(Long productId);
    boolean hasAvailableStock(Long productId, Integer requestedQuantity);
    void reserveStock(Long productId, Integer quantity);
    void releaseStock(Long productId, Integer quantity);
}
