package net.ayman.supplychainx.production.repository;

import net.ayman.supplychainx.production.model.ProductionOrder;
import net.ayman.supplychainx.production.model.ProductionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Page<ProductionOrder> findByStatus(ProductionStatus status, Pageable pageable);
}
