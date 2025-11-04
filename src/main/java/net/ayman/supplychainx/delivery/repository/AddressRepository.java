package net.ayman.supplychainx.delivery.repository;

import net.ayman.supplychainx.delivery.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
