package net.ayman.supplychainx.supply.repository;

import net.ayman.supplychainx.supply.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
}
