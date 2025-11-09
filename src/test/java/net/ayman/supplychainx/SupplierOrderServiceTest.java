package net.ayman.supplychainx;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderResponseDTO;
import net.ayman.supplychainx.supply.model.*;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.mapper.SupplierOrderMapper;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import net.ayman.supplychainx.supply.repository.SupplierOrderRepository;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import net.ayman.supplychainx.supply.service.SupplierOrderService;
import net.ayman.supplychainx.supply.service.SupplierOrderServiceImp;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Supplier order service tests")
public class SupplierOrderServiceTest {

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
}
