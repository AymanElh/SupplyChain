package net.ayman.supplychainx.delivery.service;

import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryRequestDTO;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryResponseDTO;
import net.ayman.supplychainx.delivery.mapper.DeliveryMapper;
import net.ayman.supplychainx.delivery.model.*;
import net.ayman.supplychainx.delivery.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImp implements DeliveryService {

    private final CustomerOrderRepository customerOrderRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final CustomerRepository customerRepository;

    public DeliveryServiceImp(CustomerOrderRepository customerOrderRepository, DriverRepository driverRepository, VehicleRepository vehicleRepository, DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper, CustomerRepository customerRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.customerRepository = customerRepository;
    }

    @Override
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryRequestDTO) {
        CustomerOrder order = customerOrderRepository.findById(deliveryRequestDTO.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + deliveryRequestDTO.orderId() + " not found"));

        Driver driver = driverRepository.findById(deliveryRequestDTO.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + deliveryRequestDTO.driverId() + " not found"));

        Vehicle vehicle = vehicleRepository.findById(deliveryRequestDTO.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + deliveryRequestDTO.vehicleId() + " not found"));

        if (order.getDelivery() != null) {
            throw new BusinessRuleException("Order with id " + order.getId() + " already has a delivery");
        }


        if (!order.canBeDelivered()) {
            throw new BusinessRuleException("Order with id " + order.getId() + " cannot be delivered in its current status");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDriver(driver);
        delivery.setVehicle(vehicle);
        delivery.setDeliveryDate(deliveryRequestDTO.deliveryDate());
        delivery.setStatus(DeliveryStatus.SCHEDULED);
        deliveryRepository.save(delivery);
        return deliveryMapper.toResponseDTO(delivery);
    }

    @Override
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " not found"));
        deliveryRepository.delete(delivery);
    }

    @Override
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " not found"));

        return deliveryMapper.toResponseDTO(delivery);
    }

    @Override
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable).map(deliveryMapper::toResponseDTO);
    }

    @Override
    public DeliveryResponseDTO updateDeliveryStatus(Long id, String status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " not found"));

        delivery.setStatus(DeliveryStatus.valueOf(status));
        // TODO validate status if ok
        deliveryRepository.save(delivery);
        return deliveryMapper.toResponseDTO(delivery);
    }

    @Override
    public Page<DeliveryResponseDTO> getDeliveriesByCustomerId(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        return deliveryRepository.findByOrder_CustomerId(customerId, pageable).map(deliveryMapper::toResponseDTO);
    }
}
