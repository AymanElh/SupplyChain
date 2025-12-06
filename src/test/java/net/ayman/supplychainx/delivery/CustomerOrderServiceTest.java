package net.ayman.supplychainx.delivery;

import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.OrderItem.CustomerOrderItemRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;
import net.ayman.supplychainx.delivery.mapper.CustomerOrderMapper;
import net.ayman.supplychainx.delivery.model.*;
import net.ayman.supplychainx.delivery.repository.AddressRepository;
import net.ayman.supplychainx.delivery.repository.CustomerOrderRepository;
import net.ayman.supplychainx.delivery.repository.CustomerRepository;
import net.ayman.supplychainx.delivery.service.CustomerOrderServiceImp;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.ProductRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Order Service Tests")
class CustomerOrderServiceTest {

    @Mock
    private CustomerOrderRepository customerOrderRepository;
    @Mock
    private CustomerOrderMapper customerOrderMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CustomerOrderServiceImp customerOrderService;

    private Customer customer;
    private Address address;
    private Product product;
    private CustomerOrder order;
    private CustomerOrderRequestDTO requestDTO;
    private CustomerOrderResponseDTO responseDTO;
    private CustomerOrderItem orderItem;

    private static final Long CUSTOMER_ID = 1L;
    private static final Long ADDRESS_ID = 1L;
    private static final Long PRODUCT_ID = 1L;
    private static final Long ORDER_ID = 1L;
    private static final Integer QUANTITY = 5;
    private static final Double UNIT_PRICE = 100.0;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("+1234567890");
        customer.setAddresses(new ArrayList<>());

        address = new Address();
        address.setId(ADDRESS_ID);
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setCountry("USA");
        address.setCustomer(customer);

        customer.getAddresses().add(address);

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Gaming Laptop");
        product.setStock(50);
        product.setCost(100.0);

        orderItem = new CustomerOrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(QUANTITY);
        orderItem.setUnitPrice(UNIT_PRICE);
        orderItem.calculateSubTotal();

        order = new CustomerOrder();
        order.setId(ORDER_ID);
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order.setItems(new ArrayList<>());
        order.getItems().add(orderItem);
        orderItem.setOrder(order);
        order.calculateTotalAmount();

        CustomerOrderItemRequestDTO itemRequestDTO = new CustomerOrderItemRequestDTO(
                QUANTITY,
                PRODUCT_ID,
                UNIT_PRICE
        );

        requestDTO = new CustomerOrderRequestDTO(
                CUSTOMER_ID,
                ADDRESS_ID,
                List.of(itemRequestDTO)
        );

        responseDTO = new CustomerOrderResponseDTO();
        responseDTO.setId(ORDER_ID);
        responseDTO.setStatus(OrderStatus.PENDING);
        responseDTO.setTotalAmount(500.0);
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrder() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(customerOrderRepository.save(any(CustomerOrder.class))).thenReturn(order);
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            CustomerOrderResponseDTO result = customerOrderService.createOrder(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ORDER_ID);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(addressRepository, times(1)).findById(ADDRESS_ID);
            verify(productRepository, times(2)).findById(PRODUCT_ID);
            verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
            verify(customerOrderMapper, times(1)).toResponseDTO(any(CustomerOrder.class));

            ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
            verify(customerOrderRepository).save(orderCaptor.capture());
            CustomerOrder savedOrder = orderCaptor.getValue();

            assertThat(savedOrder.getCustomer()).isEqualTo(customer);
            assertThat(savedOrder.getShippingAddress()).isEqualTo(address);
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerOrderService.createOrder(requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with id " + CUSTOMER_ID + " not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(customerOrderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFound() {
            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerOrderService.createOrder(requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address with id " + ADDRESS_ID + " not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(addressRepository, times(1)).findById(ADDRESS_ID);
            verify(customerOrderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when address doesn't belong to customer")
        void shouldThrowExceptionWhenAddressDoesNotBelongToCustomer() {
            Address otherAddress = new Address();
            otherAddress.setId(2L);
            Customer otherCustomer = new Customer();
            otherCustomer.setId(2L);
            otherCustomer.setAddresses(new ArrayList<>());
            otherAddress.setCustomer(otherCustomer);

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(otherAddress));

            assertThatThrownBy(() -> customerOrderService.createOrder(requestDTO))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("does not belong to customer");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(addressRepository, times(1)).findById(ADDRESS_ID);
            verify(customerOrderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when order items are empty")
        void shouldThrowExceptionWhenOrderItemsEmpty() {
            CustomerOrderRequestDTO emptyItemsDTO = new CustomerOrderRequestDTO(
                    CUSTOMER_ID,
                    ADDRESS_ID,
                    new ArrayList<>()
            );

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));

            assertThatThrownBy(() -> customerOrderService.createOrder(emptyItemsDTO))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Order items can't be empty");

            verify(customerOrderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when product has insufficient stock")
        void shouldThrowExceptionWhenInsufficientStock() {
            product.setStock(2);

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> customerOrderService.createOrder(requestDTO))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("has insufficient stock");

            verify(customerOrderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reduce product stock when creating order")
        void shouldReduceProductStockWhenCreatingOrder() {
            int initialStock = product.getStock();

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(customerOrderRepository.save(any(CustomerOrder.class))).thenReturn(order);
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            customerOrderService.createOrder(requestDTO);

            assertThat(product.getStock()).isEqualTo(initialStock - QUANTITY);
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by id successfully")
        void shouldGetOrderById() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            CustomerOrderResponseDTO result = customerOrderService.getOrderById(ORDER_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ORDER_ID);

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderMapper, times(1)).toResponseDTO(any(CustomerOrder.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowExceptionWhenOrderNotFound() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerOrderService.getOrderById(ORDER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order with id " + ORDER_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
        }

        @Test
        @DisplayName("Should get all orders with pagination")
        void shouldGetAllOrders() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<CustomerOrder> orderPage = new PageImpl<>(List.of(order));

            when(customerOrderRepository.findAll(pageable)).thenReturn(orderPage);
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            Page<CustomerOrderResponseDTO> result = customerOrderService.getAllOrders(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(customerOrderRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should get orders by customer id")
        void shouldGetOrdersByCustomerId() {
            List<CustomerOrder> orders = Arrays.asList(order);

            when(customerOrderRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(customerOrderRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(orders);
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            List<CustomerOrderResponseDTO> result = customerOrderService.getOrdersByCustomerId(CUSTOMER_ID);

            assertThat(result).isNotNull();
            assertThat(result.size()).isOne();

            verify(customerOrderRepository, times(1)).existsById(CUSTOMER_ID);
            verify(customerOrderRepository, times(1)).findByCustomerId(CUSTOMER_ID);
        }
    }

    @Nested
    @DisplayName("Update Order Tests")
    class UpdateOrderTests {

        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatus() {
            OrderStatus newStatus = OrderStatus.IN_PREPARATION;

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(customerOrderRepository.save(any(CustomerOrder.class))).thenReturn(order);
            when(customerOrderMapper.toResponseDTO(any(CustomerOrder.class))).thenReturn(responseDTO);

            CustomerOrderResponseDTO result = customerOrderService.updateStatus(ORDER_ID, newStatus);

            assertThat(result).isNotNull();

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderRepository, times(1)).save(order);

            ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
            verify(customerOrderRepository).save(orderCaptor.capture());
            CustomerOrder updatedOrder = orderCaptor.getValue();

            assertThat(updatedOrder.getStatus()).isEqualTo(newStatus);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existing order")
        void shouldThrowExceptionWhenUpdatingNonExistingOrder() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerOrderService.updateStatus(ORDER_ID, OrderStatus.DELIVERED))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order wtih id " + ORDER_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Order Tests")
    class DeleteOrderTests {

        @Test
        @DisplayName("Should delete order successfully when status is CANCELLED")
        void shouldDeleteOrderWhenCancelled() {
            order.setStatus(OrderStatus.CANCELLED);

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            doNothing().when(customerOrderRepository).delete(order);

            customerOrderService.deleteOrder(ORDER_ID);

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderRepository, times(1)).delete(order);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when trying to delete order with PENDING status")
        void shouldThrowExceptionWhenDeletingPendingOrder() {
            order.setStatus(OrderStatus.PENDING);

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> customerOrderService.deleteOrder(ORDER_ID))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot delete an order with status");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should restore product stock when deleting order")
        void shouldRestoreProductStockWhenDeletingOrder() {
            int initialStock = product.getStock();
            order.setStatus(OrderStatus.CANCELLED);

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            doNothing().when(customerOrderRepository).delete(order);

            customerOrderService.deleteOrder(ORDER_ID);

            assertThat(product.getStock()).isEqualTo(initialStock + QUANTITY);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing order")
        void shouldThrowExceptionWhenDeletingNonExistingOrder() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerOrderService.deleteOrder(ORDER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order with id " + ORDER_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(customerOrderRepository, never()).delete(any());
        }
    }
}
