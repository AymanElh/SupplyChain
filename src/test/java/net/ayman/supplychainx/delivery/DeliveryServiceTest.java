package net.ayman.supplychainx.delivery;

import net.ayman.supplychainx.common.exception.BusinessRuleException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryRequestDTO;
import net.ayman.supplychainx.delivery.dto.delivery.DeliveryResponseDTO;
import net.ayman.supplychainx.delivery.mapper.DeliveryMapper;
import net.ayman.supplychainx.delivery.model.*;
import net.ayman.supplychainx.delivery.repository.*;
import net.ayman.supplychainx.delivery.service.DeliveryServiceImp;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delivery Service Tests")
class DeliveryServiceTest {

    @Mock
    private CustomerOrderRepository customerOrderRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private DeliveryMapper deliveryMapper;
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DeliveryServiceImp deliveryService;

    private CustomerOrder order;
    private Driver driver;
    private Vehicle vehicle;
    private Delivery delivery;
    private Customer customer;
    private DeliveryRequestDTO requestDTO;
    private DeliveryResponseDTO responseDTO;

    private static final Long ORDER_ID = 1L;
    private static final Long DRIVER_ID = 1L;
    private static final Long VEHICLE_ID = 1L;
    private static final Long DELIVERY_ID = 1L;
    private static final Long CUSTOMER_ID = 1L;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("+1234567890");

        Address address = new Address();
        address.setId(1L);
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setCountry("USA");

        order = new CustomerOrder();
        order.setId(ORDER_ID);
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.READY);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(500.0);

        driver = new Driver();
        driver.setId(DRIVER_ID);
        driver.setName("Mike Driver");
        driver.setPhone("+1234567890");
        driver.setLicenseNumber("DL123456");
        driver.setIsAvailable(true);

        vehicle = new Vehicle();
        vehicle.setId(VEHICLE_ID);
        vehicle.setLicensePlate("ABC-1234");
        vehicle.setType("Van");
        vehicle.setModel("Toyota Hiace");

        delivery = new Delivery();
        delivery.setId(DELIVERY_ID);
        delivery.setOrder(order);
        delivery.setDriver(driver);
        delivery.setVehicle(vehicle);
        delivery.setStatus(DeliveryStatus.SCHEDULED);
        delivery.setDeliveryDate(LocalDate.now().plusDays(2));

        requestDTO = new DeliveryRequestDTO(
                ORDER_ID,
                DRIVER_ID,
                VEHICLE_ID,
                LocalDate.now().plusDays(2)
        );

        responseDTO = new DeliveryResponseDTO(
                DELIVERY_ID,
                new DeliveryResponseDTO.OrderInfo(ORDER_ID, "Customer Order", 500.0),
                new DeliveryResponseDTO.DriverInfo(DRIVER_ID, "Mike Driver", "+1234567890"),
                new DeliveryResponseDTO.VehicleInfo(VEHICLE_ID, "ABC-1234"),
                DeliveryStatus.SCHEDULED,
                LocalDate.now().plusDays(2)
        );
    }

    @Nested
    @DisplayName("Create Delivery Tests")
    class CreateDeliveryTests {

        @Test
        @DisplayName("Should create delivery successfully")
        void shouldCreateDelivery() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
            when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
            when(deliveryMapper.toResponseDTO(any(Delivery.class))).thenReturn(responseDTO);

            DeliveryResponseDTO result = deliveryService.createDelivery(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(DELIVERY_ID);
            assertThat(result.status()).isEqualTo(DeliveryStatus.SCHEDULED);

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(deliveryRepository, times(1)).save(any(Delivery.class));
            verify(deliveryMapper, times(1)).toResponseDTO(any(Delivery.class));

            ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
            verify(deliveryRepository).save(deliveryCaptor.capture());
            Delivery savedDelivery = deliveryCaptor.getValue();

            assertThat(savedDelivery.getOrder()).isEqualTo(order);
            assertThat(savedDelivery.getDriver()).isEqualTo(driver);
            assertThat(savedDelivery.getVehicle()).isEqualTo(vehicle);
            assertThat(savedDelivery.getStatus()).isEqualTo(DeliveryStatus.SCHEDULED);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowExceptionWhenOrderNotFound() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.createDelivery(requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order with id " + ORDER_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(deliveryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when driver not found")
        void shouldThrowExceptionWhenDriverNotFound() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.createDelivery(requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Driver with id " + DRIVER_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(deliveryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when vehicle not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.createDelivery(requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Vehicle with id " + VEHICLE_ID + " not found");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(deliveryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when order already has a delivery")
        void shouldThrowExceptionWhenOrderAlreadyHasDelivery() {
            order.setDelivery(delivery);

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));

            assertThatThrownBy(() -> deliveryService.createDelivery(requestDTO))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("already has a delivery");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(deliveryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when order cannot be delivered")
        void shouldThrowExceptionWhenOrderCannotBeDelivered() {
            order.setStatus(OrderStatus.PENDING);

            when(customerOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));

            assertThatThrownBy(() -> deliveryService.createDelivery(requestDTO))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("cannot be delivered in its current status");

            verify(customerOrderRepository, times(1)).findById(ORDER_ID);
            verify(deliveryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Delivery Tests")
    class GetDeliveryTests {

        @Test
        @DisplayName("Should get delivery by id successfully")
        void shouldGetDeliveryById() {
            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
            when(deliveryMapper.toResponseDTO(any(Delivery.class))).thenReturn(responseDTO);

            DeliveryResponseDTO result = deliveryService.getDeliveryById(DELIVERY_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(DELIVERY_ID);

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
            verify(deliveryMapper, times(1)).toResponseDTO(any(Delivery.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when delivery not found")
        void shouldThrowExceptionWhenDeliveryNotFound() {
            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.getDeliveryById(DELIVERY_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Delivery with id " + DELIVERY_ID + " not found");

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
        }

        @Test
        @DisplayName("Should get all deliveries with pagination")
        void shouldGetAllDeliveries() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Delivery> deliveryPage = new PageImpl<>(List.of(delivery));

            when(deliveryRepository.findAll(pageable)).thenReturn(deliveryPage);
            when(deliveryMapper.toResponseDTO(any(Delivery.class))).thenReturn(responseDTO);

            Page<DeliveryResponseDTO> result = deliveryService.getAllDeliveries(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(deliveryRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should get deliveries by customer id")
        void shouldGetDeliveriesByCustomerId() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Delivery> deliveryPage = new PageImpl<>(List.of(delivery));

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
            when(deliveryRepository.findByOrder_CustomerId(CUSTOMER_ID, pageable)).thenReturn(deliveryPage);
            when(deliveryMapper.toResponseDTO(any(Delivery.class))).thenReturn(responseDTO);

            Page<DeliveryResponseDTO> result = deliveryService.getDeliveriesByCustomerId(CUSTOMER_ID, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(deliveryRepository, times(1)).findByOrder_CustomerId(CUSTOMER_ID, pageable);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFoundForDeliveries() {
            Pageable pageable = PageRequest.of(0, 10);

            when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.getDeliveriesByCustomerId(CUSTOMER_ID, pageable))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer with id " + CUSTOMER_ID + " not found");

            verify(customerRepository, times(1)).findById(CUSTOMER_ID);
            verify(deliveryRepository, never()).findByOrder_CustomerId(any(), any());
        }
    }

    @Nested
    @DisplayName("Update Delivery Tests")
    class UpdateDeliveryTests {

        @Test
        @DisplayName("Should update delivery status successfully")
        void shouldUpdateDeliveryStatus() {
            String newStatus = "IN_PROGRESS";

            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
            when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
            when(deliveryMapper.toResponseDTO(any(Delivery.class))).thenReturn(responseDTO);

            DeliveryResponseDTO result = deliveryService.updateDeliveryStatus(DELIVERY_ID, newStatus);

            assertThat(result).isNotNull();

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
            verify(deliveryRepository, times(1)).save(delivery);

            ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
            verify(deliveryRepository).save(deliveryCaptor.capture());
            Delivery updatedDelivery = deliveryCaptor.getValue();

            assertThat(updatedDelivery.getStatus()).isEqualTo(DeliveryStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existing delivery")
        void shouldThrowExceptionWhenUpdatingNonExistingDelivery() {
            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.updateDeliveryStatus(DELIVERY_ID, "DELIVERED"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Delivery with id " + DELIVERY_ID + " not found");

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
            verify(deliveryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Delivery Tests")
    class DeleteDeliveryTests {

        @Test
        @DisplayName("Should delete delivery successfully")
        void shouldDeleteDelivery() {
            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
            doNothing().when(deliveryRepository).delete(delivery);

            deliveryService.deleteDelivery(DELIVERY_ID);

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
            verify(deliveryRepository, times(1)).delete(delivery);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing delivery")
        void shouldThrowExceptionWhenDeletingNonExistingDelivery() {
            when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.deleteDelivery(DELIVERY_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Delivery with id " + DELIVERY_ID + " not found");

            verify(deliveryRepository, times(1)).findById(DELIVERY_ID);
            verify(deliveryRepository, never()).delete(any());
        }
    }
}
