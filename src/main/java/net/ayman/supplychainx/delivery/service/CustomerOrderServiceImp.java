package net.ayman.supplychainx.delivery.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;
import net.ayman.supplychainx.delivery.mapper.CustomerOrderMapper;
import net.ayman.supplychainx.delivery.model.*;
import net.ayman.supplychainx.delivery.repository.AddressRepository;
import net.ayman.supplychainx.delivery.repository.CustomerOrderRepository;
import net.ayman.supplychainx.delivery.repository.CustomerRepository;
import net.ayman.supplychainx.production.api.ProductFacade;
import net.ayman.supplychainx.production.dto.product.ProductResponseDTO;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImp implements CustomerOrderService {


    private final CustomerOrderMapper customerOrderMapper;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductFacade productFacade;


    @Override
    @Transactional
    public CustomerOrderResponseDTO createOrder(CustomerOrderRequestDTO dto) {
        log.debug("Creating new order: {}", dto);
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + dto.customerId() + " not found"));

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + dto.addressId() + " not found"));

        if (!customer.getAddresses().contains(address)) {
            throw new BusinessRuleException("Address with id " + dto.addressId() +
                    " does not belong to customer " + customer.getName());
        }

        if (dto.orderItems() == null || dto.orderItems().isEmpty()) {
            throw new BusinessRuleException("Order items can't be empty");
        }

        Map<Long, ProductResponseDTO> productCache = new HashMap<>();

        for (var item : dto.orderItems()) {
            ProductResponseDTO product = productCache.computeIfAbsent(
                    item.productId(),
                    id -> productFacade.getProductById(item.productId())
            );

//            if (product.getStock() < item.quantity()) {
//                throw new BusinessRuleException("Product " + product.getName() + " has insufficient stock");
//            }

            if (!productFacade.hasAvailableStock(product.getId(), item.quantity())) {
                throw new BusinessRuleException("Product " + product.getName() + " has insufficient stock");
            }
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());

        for (var item : dto.orderItems()) {
            ProductResponseDTO product = productCache.get(item.productId());

            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(product.getCost());
            orderItem.calculateSubTotal();

//            product.setStock(product.getStock() - item.quantity());
            productFacade.reserveStock(product.getId(), item.quantity());
            order.addOrderItem(orderItem);
        }

//        CustomerOrder order = customerOrderMapper.toEntity(dto);
        order.calculateTotalAmount();
        log.debug("Order to be saved: {}", order);
        return customerOrderMapper.toResponseDTO(customerOrderRepository.save(order));
    }

    @Override
    public CustomerOrderResponseDTO getOrderById(Long id) {
        log.debug("Getting order with id {}", id);
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        log.debug("Found order: {}", order);
        return customerOrderMapper.toResponseDTO(order);
    }

    @Override
    public CustomerOrderResponseDTO updateQuantity(Long orderId, Long quantity) {
        return null;
    }

    @Override
    @Transactional
    public CustomerOrderResponseDTO updateStatus(Long orderId, OrderStatus status) {
        log.debug("Updating status of order with id {} to {}", orderId, status);
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order wtih id " + orderId + " not found"));

        order.setStatus(status);
        log.debug("Order updated: {}", order);
        customerOrderRepository.save(order);
        return customerOrderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));

        if(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.IN_WAY || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Cannot delete an order with status " + order.getStatus());
        }



        order.setStatus(OrderStatus.CANCELLED);

        for (CustomerOrderItem item: order.getItems()) {
//            Product product = item.getProduct();
//            product.setStock(product.getStock() + item.getQuantity());
//            productRepository.save(product);

            productFacade.releaseStock(item.getProductId(), item.getQuantity());
        }

        customerOrderRepository.delete(order);
        log.debug("Order with id {} deleted", id);
    }

    @Override
    public Page<CustomerOrderResponseDTO> getAllOrders(Pageable pageable) {
        return customerOrderRepository.findAll(pageable).map(customerOrderMapper::toResponseDTO);
    }

    @Override
    public Page<CustomerOrderResponseDTO> getAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        return customerOrderRepository.findAllByStatus(status, pageable).map(customerOrderMapper::toResponseDTO);
    }

    @Override
    public List<CustomerOrderResponseDTO> getOrdersByCustomerId(Long customerId) {
        if(!customerOrderRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer with id " + customerId + " not found");
        }

        List<CustomerOrder> orders = customerOrderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(customerOrderMapper::toResponseDTO)
                .toList();
    }
}
