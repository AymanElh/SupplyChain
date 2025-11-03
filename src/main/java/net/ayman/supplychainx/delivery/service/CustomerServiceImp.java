package net.ayman.supplychainx.delivery.service;

import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.address.AddressRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.mapper.AddressMapper;
import net.ayman.supplychainx.delivery.mapper.CustomerMapper;
import net.ayman.supplychainx.delivery.model.Address;
import net.ayman.supplychainx.delivery.model.Customer;
import net.ayman.supplychainx.delivery.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;

    public CustomerServiceImp(CustomerRepository customerRepository, CustomerMapper customerMapper, AddressMapper addressMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public CustomerResponseDTO addCustomer(CustomerRequestDTO customerRequestDTO) {
        log.debug("Adding new customer: {}", customerRequestDTO);
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        log.debug("Customer to be saved: {}", customer);
        if (customer.getAddresses() != null) {
            customer.getAddresses().forEach(address -> address.setCustomer(customer));
        }
        return customerMapper.toResponseDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with this id not found"));
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponseDTO);
    }

    @Override
    public CustomerResponseDTO editCustomerInfo(Long id, CustomerRequestDTO customerRequestDTO) {
        log.debug("Editing customer with id {}: {}", id, customerRequestDTO);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with this id not found"));
        customerMapper.updateEntityToDTO(customerRequestDTO, customer);
        log.debug("Editing customer: {}", customer);
        return customerMapper.toResponseDTO(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with this id " + id + " not found"));
        log.debug("Deleting customer {}", customer);
        customerRepository.delete(customer);
    }

    @Override
    public CustomerResponseDTO addAddressesToCustomer(Long customerId, AddressRequestDTO address) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with this id " + customerId + " not found"));
        customer.addAddressToCustomer(addressMapper.toEntity(address));
        customerRepository.save(customer);
        return customerMapper.toResponseDTO(customer);
    }
}
