package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.delivery.dto.address.AddressRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO addCustomer(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO editCustomerInfo(Long id, CustomerRequestDTO customerRequestDTO);
    void deleteCustomer(Long id);
    CustomerResponseDTO addAddressesToCustomer(Long customerId, AddressRequestDTO addresses);
}
