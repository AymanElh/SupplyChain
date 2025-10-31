package net.ayman.supplychainx.supply.repository;

import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import net.ayman.supplychainx.supply.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
}
