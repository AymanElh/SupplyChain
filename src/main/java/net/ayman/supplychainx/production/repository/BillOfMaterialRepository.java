package net.ayman.supplychainx.production.repository;

import net.ayman.supplychainx.production.model.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    boolean existsByProductIdAndMaterialId(Long productId, Long materialId);

    List<BillOfMaterial> findByProductId(Long productId);
}
