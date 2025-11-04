package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Customer;
import net.ayman.supplychainx.delivery.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository <CustomerOrder, Long> {
    List<CustomerOrder> findByCustomerId(Long customerId);
}
