package net.ayman.supplychainx.supply.repository;

import net.ayman.supplychainx.supply.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
}
