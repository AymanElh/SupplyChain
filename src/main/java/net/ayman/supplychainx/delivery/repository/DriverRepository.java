package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
