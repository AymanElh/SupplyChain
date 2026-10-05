package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Customer;
import net.ayman.supplychainx.delivery.model.CustomerOrder;
import net.ayman.supplychainx.delivery.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository <CustomerOrder, Long> {
    List<CustomerOrder> findByCustomerId(Long customerId);

    Page<CustomerOrder> findAllByStatus(OrderStatus status, Pageable pageable);
}
