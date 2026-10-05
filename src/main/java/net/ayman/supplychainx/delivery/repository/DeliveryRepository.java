package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Page<Delivery> findByOrder_CustomerId(Long orderCustomerId, Pageable pageable);
}
