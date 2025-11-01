package net.ayman.supplychainx.production.repository;

import net.ayman.supplychainx.production.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}
