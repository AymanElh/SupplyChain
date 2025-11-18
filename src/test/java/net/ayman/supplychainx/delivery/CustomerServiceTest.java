package net.ayman.supplychainx.delivery;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.address.AddressRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.mapper.AddressMapper;
import net.ayman.supplychainx.delivery.mapper.CustomerMapper;
import net.ayman.supplychainx.delivery.model.Address;
import net.ayman.supplychainx.delivery.model.Customer;
import net.ayman.supplychainx.delivery.repository.CustomerRepository;
import net.ayman.supplychainx.delivery.service.CustomerServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private CustomerServiceImp customerService;

    private Customer customer;
    private CustomerRequestDTO requestDTO;
    private CustomerResponseDTO responseDTO;
    private Address address;
    private AddressRequestDTO addressRequestDTO;

    private static final Long CUSTOMER_ID = 1L;
    private static final String CUSTOMER_NAME = "John Doe";
    private static final String PHONE = "+1234567890";
    private static final String EMAIL = "john@example.com";
    private static final Long ADDRESS_ID = 1L;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setPhone(PHONE);
        customer.setEmail(EMAIL);
        customer.setAddresses(new ArrayList<>());

        address = new Address();
        address.setId(ADDRESS_ID);
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setCountry("USA");
        address.setCustomer(customer);

        requestDTO = new CustomerRequestDTO(CUSTOMER_NAME, PHONE, EMAIL, null);

        responseDTO = new CustomerResponseDTO(CUSTOMER_ID, CUSTOMER_NAME, PHONE, EMAIL, null, null, null, null);

        addressRequestDTO = new AddressRequestDTO("USA", "10001", "NY", "New York", "123 Main St");
    }

    @Nested
    @DisplayName("Add Customer Tests")
    class AddCustomerTests {

        @Test
        @DisplayName("Should add customer successfully")
        void shouldAddCustomer() {
            when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            CustomerResponseDTO result = customerService.addCustomer(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(CUSTOMER_ID);
            assertThat(result.name()).isEqualTo(CUSTOMER_NAME);
            assertThat(result.phone()).isEqualTo(PHONE);
            assertThat(result.email()).isEqualTo(EMAIL);

            verify(customerMapper, times(1)).toEntity(requestDTO);
            verify(customerRepository, times(1)).save(any(Customer.class));
            verify(customerMapper, times(1)).toResponseDTO(any(Customer.class));
        }

        @Test
        @DisplayName("Should set customer reference on addresses when adding customer")
        void shouldSetCustomerReferenceOnAddresses() {
            customer.getAddresses().add(address);

            when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            customerService.addCustomer(requestDTO);

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).save(customerCaptor.capture());
            Customer savedCustomer = customerCaptor.getValue();

            assertThat(savedCustomer.getAddresses().size()).isEqualTo(1);
            assertThat(savedCustomer.getAddresses().get(0).getCustomer()).isEqualTo(customer);
        }

        @Test
        @DisplayName("Should handle customer without addresses")
        void shouldHandleCustomerWithoutAddresses() {
            customer.setAddresses(null);

            when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            CustomerResponseDTO result = customerService.addCustomer(requestDTO);

            assertThat(result).isNotNull();
            verify(customerRepository, times(1)).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Get Customer Tests")
    class GetCustomerTests {

        @Test
        @DisplayName("Should get customer by id successfully")
        void shouldGetCustomerById() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            CustomerResponseDTO result = customerService.getCustomerById(CUSTOMER_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(CUSTOMER_ID);
            assertThat(result.name()).isEqualTo(CUSTOMER_NAME);

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerMapper, times(1)).toResponseDTO(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomerById(CUSTOMER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with this id not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerMapper, never()).toResponseDTO(any());
        }

        @Test
        @DisplayName("Should get all customers with pagination")
        void shouldGetAllCustomers() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> customerPage = new PageImpl<>(List.of(customer));

            when(customerRepository.findAll(pageable)).thenReturn(customerPage);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            Page<CustomerResponseDTO> result = customerService.getAllCustomers(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(customerRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Update Customer Tests")
    class UpdateCustomerTests {

        @Test
        @DisplayName("Should update customer info successfully")
        void shouldUpdateCustomerInfo() {
            CustomerRequestDTO updateDTO = new CustomerRequestDTO("Updated Name", "+9999999999", "updated@example.com", null);

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            doNothing().when(customerMapper).updateEntityToDTO(updateDTO, customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            CustomerResponseDTO result = customerService.editCustomerInfo(CUSTOMER_ID, updateDTO);

            assertThat(result).isNotNull();

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerMapper, times(1)).updateEntityToDTO(updateDTO, customer);
            verify(customerRepository, times(1)).save(customer);
            verify(customerMapper, times(1)).toResponseDTO(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existing customer")
        void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.editCustomerInfo(CUSTOMER_ID, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with this id not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should call mapper to update entity from DTO")
        void shouldCallMapperToUpdateEntity() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            doNothing().when(customerMapper).updateEntityToDTO(requestDTO, customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            customerService.editCustomerInfo(CUSTOMER_ID, requestDTO);

            ArgumentCaptor<CustomerRequestDTO> dtoCaptor = ArgumentCaptor.forClass(CustomerRequestDTO.class);
            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerMapper).updateEntityToDTO(dtoCaptor.capture(), customerCaptor.capture());

            assertThat(dtoCaptor.getValue()).isEqualTo(requestDTO);
            assertThat(customerCaptor.getValue()).isEqualTo(customer);
        }
    }

    @Nested
    @DisplayName("Delete Customer Tests")
    class DeleteCustomerTests {

        @Test
        @DisplayName("Should delete customer successfully")
        void shouldDeleteCustomer() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            doNothing().when(customerRepository).delete(customer);

            customerService.deleteCustomer(CUSTOMER_ID);

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerRepository, times(1)).delete(customer);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing customer")
        void shouldThrowExceptionWhenDeletingNonExistingCustomer() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.deleteCustomer(CUSTOMER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with this id " + CUSTOMER_ID + " not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Add Address Tests")
    class AddAddressTests {

        @Test
        @DisplayName("Should add address to customer successfully")
        void shouldAddAddressToCustomer() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressMapper.toEntity(addressRequestDTO)).thenReturn(address);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            CustomerResponseDTO result = customerService.addAddressesToCustomer(CUSTOMER_ID, addressRequestDTO);

            assertThat(result).isNotNull();

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(addressMapper, times(1)).toEntity(addressRequestDTO);
            verify(customerRepository, times(1)).save(customer);
            verify(customerMapper, times(1)).toResponseDTO(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when customer not found for adding address")
        void shouldThrowExceptionWhenCustomerNotFoundForAddingAddress() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.addAddressesToCustomer(CUSTOMER_ID, addressRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with this id " + CUSTOMER_ID + " not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should call addAddressToCustomer method")
        void shouldCallAddAddressToCustomerMethod() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressMapper.toEntity(addressRequestDTO)).thenReturn(address);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            customerService.addAddressesToCustomer(CUSTOMER_ID, addressRequestDTO);

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).save(customerCaptor.capture());
            Customer savedCustomer = customerCaptor.getValue();

            assertThat(savedCustomer.getAddresses().size()).isEqualTo(1);
            assertThat(savedCustomer.getAddresses().get(0)).isEqualTo(address);
        }
    }
}
