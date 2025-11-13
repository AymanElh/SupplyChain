package net.ayman.supplychainx.supply;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.model.*;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.mapper.SupplierOrderMapper;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import net.ayman.supplychainx.supply.repository.SupplierOrderRepository;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import net.ayman.supplychainx.supply.service.SupplierOrderServiceImp;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Supplier order service tests")
class SupplierOrderServiceTest {

    @Mock private SupplierOrderRepository orderRepository;

    @Mock private SupplierRepository supplierRepository;

    @Mock private RawMaterialRepository materialRepository;

    @Mock private SupplierOrderMapper orderMapper;

    @InjectMocks
    private SupplierOrderServiceImp orderService;

    private Supplier supplier;
    private RawMaterial material1;
    private RawMaterial material2;
    private SupplierOrder order;
    private SupplierOrderRequestDTO orderRequest;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
        supplier.setEmail("supplier@test.com");

        // Set up materials
        material1 = new RawMaterial();
        material1.setId(1L);
        material1.setName("Plastic Resin");
        material1.setStock(500);
        material1.setUnitCost(15.50);

        material2 = new RawMaterial();
        material2.setId(2L);
        material2.setName("Aluminum");
        material2.setStock(300);
        material2.setUnitCost(25.00);

        supplier.setMaterials(Arrays.asList(material1, material2));


        order = new SupplierOrder();
        order.setId(1L);
        order.setSupplier(supplier);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDate(LocalDate.now());

        // Set up order request DTO
        SupplierOrderItemRequestDTO item1 = new SupplierOrderItemRequestDTO();
        item1.setMaterialId(1L);
        item1.setQuantity(100);
        item1.setUnitPrice(15.50);

        SupplierOrderItemRequestDTO item2 = new SupplierOrderItemRequestDTO();
        item2.setMaterialId(2L);
        item2.setQuantity(50);
        item2.setUnitPrice(25.00);

        orderRequest = new SupplierOrderRequestDTO();
        orderRequest.setSupplierId(1L);
        orderRequest.setItems(Arrays.asList(item1, item2));

    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully with valid data")
        void shouldCreateOrder_WithValidData() {

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(materialRepository.findById(1L)).thenReturn(Optional.of(material1));
            when(materialRepository.findById(2L)).thenReturn(Optional.of(material2));
            when(orderRepository.save(any(SupplierOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            SupplierOrderResponseDTO result = orderService.createOrder(orderRequest);

            assertThat(result).isNotNull();

            verify(supplierRepository, times(1)).findById(1L);

            verify(materialRepository, times(1)).findById(1L);
            verify(materialRepository, times(1)).findById(2L);

            verify(orderRepository, times(1)).save(any(SupplierOrder.class));

            ArgumentCaptor<SupplierOrder> orderCaptor = ArgumentCaptor.forClass(SupplierOrder.class);
            verify(orderRepository).save(orderCaptor.capture());
            SupplierOrder savedOrder = orderCaptor.getValue();

            assertThat(savedOrder.getSupplier()).isEqualTo(supplier);
            assertThat(savedOrder.getItems()).hasSize(2);
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);

            // Verify total amount calculation
            // Item1: 100 × 15.50 = 1550.00
            // Item2: 50 × 25.00 = 1250.00
            // Total: 2800.00
            assertThat(savedOrder.getAmount()).isEqualTo(2800.00);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowException_WhenSupplierNotFound() {
            // Given
            when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Supplier");

            verify(supplierRepository, times(1)).findById(1L);
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when material not found")
        void shouldThrowException_WhenMaterialNotFound() {
            // Given
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(materialRepository.findById(1L)).thenReturn(Optional.of(material1));
            when(materialRepository.findById(2L)).thenReturn(Optional.empty()); // Material 2 not found

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Material");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when material not supplied by supplier")
        void shouldThrowException_WhenMaterialNotSuppliedBySupplier() {
            // Given
            RawMaterial material3 = new RawMaterial();
            material3.setId(3L);
            material3.setName("Steel");

            // Update request to include material not supplied by this supplier
            SupplierOrderItemRequestDTO item3 = new SupplierOrderItemRequestDTO();
            item3.setMaterialId(3L);
            item3.setQuantity(10);
            item3.setUnitPrice(50.00);

            orderRequest.setItems(List.of(item3));

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(materialRepository.findById(3L)).thenReturn(Optional.of(material3));

            assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not available from supplier");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should calculate subtotals correctly for each item")
        void shouldCalculateSubTotalsCorrectly() {
            // Given
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(materialRepository.findById(1L)).thenReturn(Optional.of(material1));
            when(materialRepository.findById(2L)).thenReturn(Optional.of(material2));
            when(orderRepository.save(any(SupplierOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            orderService.createOrder(orderRequest);

            ArgumentCaptor<SupplierOrder> orderCaptor = ArgumentCaptor.forClass(SupplierOrder.class);
            verify(orderRepository).save(orderCaptor.capture());
            SupplierOrder savedOrder = orderCaptor.getValue();

            List<SupplierOrderItem> items = savedOrder.getItems();

            SupplierOrderItem item1 = items.get(0);
            assertThat(item1.getQuantity()).isEqualTo(100);
            assertThat(item1.getUnitPrice()).isEqualTo(15.50);
            assertThat(item1.getSubTotal()).isEqualTo(1550.00);

            SupplierOrderItem item2 = items.get(1);
            assertThat(item2.getQuantity()).isEqualTo(50);
            assertThat(item2.getUnitPrice()).isEqualTo(25.00);
            assertThat(item2.getSubTotal()).isEqualTo(1250.00);
        }
    }

    @Nested
    @DisplayName("Update Order Tests")
    class UpdateOrderTests {
        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatusSuccessfully() {
            order.setStatus(OrderStatus.WAITING);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(SupplierOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            // When
            SupplierOrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.RECEIVED);

            // Then
            assertThat(result).isNotNull();

            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(SupplierOrder.class));

            ArgumentCaptor<SupplierOrder> orderCaptor = ArgumentCaptor.forClass(SupplierOrder.class);
            verify(orderRepository).save(orderCaptor.capture());
            SupplierOrder updatedOrder = orderCaptor.getValue();

            assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        }

        @Test
        @DisplayName("Should not update order status if already RECEIVED")
        void shouldNotUpdate_WhenOrderAlreadyReceived() {
            order.setStatus(OrderStatus.RECEIVED);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.IN_PROGRESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order is already received and cannot be updated");


            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, never()).save(any());
        }


        @Test
        @DisplayName("Should receive order and update stock levels")
        void shouldReceiveOrderAndUpdateStock_WhenOrderIsWaiting() {
            RawMaterial mat1 = RawMaterial.builder()
                    .id(1L)
                    .name("Material 1")
                    .stock(200)
                    .build();

            RawMaterial mat2 = RawMaterial.builder()
                    .id(2L)
                    .name("Material 2")
                    .stock(100)
                    .build();

            SupplierOrderItem item1 = SupplierOrderItem.builder()
                    .id(1L)
                    .rawMaterial(mat1)
                    .quantity(50)
                    .unitPrice(10.0)
                    .subTotal(500.0)
                    .build();

            SupplierOrderItem item2 = SupplierOrderItem.builder()
                    .id(2L)
                    .rawMaterial(mat2)
                    .quantity(30)
                    .unitPrice(20.0)
                    .subTotal(600.0)
                    .build();

            order.setStatus(OrderStatus.IN_PROGRESS);
            order.setItems(Arrays.asList(item1, item2));

            item1.setOrder(order);
            item2.setOrder(order);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(SupplierOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            SupplierOrderResponseDTO result = orderService.receiveOrder(1L);

            ArgumentCaptor<SupplierOrder> orderCaptor = ArgumentCaptor.forClass(SupplierOrder.class);
            verify(orderRepository).save(orderCaptor.capture());
            SupplierOrder updatedOrder = orderCaptor.getValue();
            assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.RECEIVED);

            assertThat(result).isNotNull();
            assertThat(mat1.getStock()).isEqualTo(250);
            assertThat(mat2.getStock()).isEqualTo(130);
        }

        @Test
        @DisplayName("Should throw exception when receiving an already RECEIVED order")
        void updateOrderWithStatusReceived_ShouldThrowException() {
            order.setStatus(OrderStatus.RECEIVED);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.receiveOrder(1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order already received");

            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Order Tests")
    class DeleteOrderTests {
        @Test
        @DisplayName("Should delete order successfully")
        void deleteOrder_successfully() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.deleteOrder(1L);
            System.out.println("order isDeleted: " + order.isDeleted());
            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(SupplierOrder.class));
        }

        @Test
        @DisplayName("Should not delete order if already RECEIVED")
        void deleteOrder_whenAlreadyReceived_ShouldThrowException() {
            order.setStatus(OrderStatus.RECEIVED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.deleteOrder(1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot delete received order");

            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Read Order Tests")
    class ReadOrderTests {
        @Test
        @DisplayName("Should get all orders successfully")
        void getAllOrders_successfully() {
            SupplierOrder anotherOrder = new SupplierOrder();
            anotherOrder.setId(2L);
            anotherOrder.setSupplier(supplier);
            anotherOrder.setStatus(OrderStatus.IN_PROGRESS);
            anotherOrder.setOrderDate(LocalDate.now());

            List<SupplierOrder> orders = Arrays.asList(order, anotherOrder);

            Page<SupplierOrder> page = new PageImpl<>(orders);

            when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            Pageable pageable = PageRequest.of(1, 10);
            Page<SupplierOrderResponseDTO> results = orderService.getAll(pageable);

            assertThat(results.getContent()).isNotNull();
            assertThat(results.getContent()).hasSize(2);

            verify(orderRepository, times(1)).findAll(pageable);
            verify(orderMapper, times(2)).toResponseDTO(any(SupplierOrder.class));
        }

        @Test
        @DisplayName("Should get order by ID successfully")
        void getById_successfully() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderMapper.toResponseDTO(any(SupplierOrder.class)))
                    .thenReturn(new SupplierOrderResponseDTO());

            SupplierOrderResponseDTO result = orderService.getById(1L);

            assertThat(result).isNotNull();

            verify(orderRepository, times(1)).findById(1L);
            verify(orderMapper, times(1)).toResponseDTO(any(SupplierOrder.class));
        }
    }
}
