package net.ayman.supplychainx.supply.repository;

import net.ayman.supplychainx.supply.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    boolean existsByName(String name);
}
