package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
