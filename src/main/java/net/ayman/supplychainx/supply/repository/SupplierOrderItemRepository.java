package net.ayman.supplychainx.supply.repository;

import net.ayman.supplychainx.supply.model.SupplierOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierOrderItemRepository extends JpaRepository<SupplierOrderItem, Long> {
}
