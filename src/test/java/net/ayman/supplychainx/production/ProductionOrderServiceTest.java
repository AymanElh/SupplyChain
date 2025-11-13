package net.ayman.supplychainx.production;

import net.ayman.supplychainx.production.dto.order.ProductionOrderRequestDTO;
import net.ayman.supplychainx.production.dto.order.ProductionOrderResponseDTO;
import net.ayman.supplychainx.production.mapper.ProductionOrderMapper;
import net.ayman.supplychainx.production.model.BillOfMaterial;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.model.ProductionOrder;
import net.ayman.supplychainx.production.model.ProductionStatus;
import net.ayman.supplychainx.production.repository.BillOfMaterialRepository;
import net.ayman.supplychainx.production.repository.ProductRepository;
import net.ayman.supplychainx.production.repository.ProductionOrderRepository;
import net.ayman.supplychainx.production.service.ProductionOrderServiceImp;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Production order service tests")
class ProductionOrderServiceTest {

    @Mock private ProductionOrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private BillOfMaterialRepository bomRepository;
    @Mock private RawMaterialRepository rawMaterialRepository;
    @Mock private ProductionOrderMapper orderMapper;

    @InjectMocks
    private ProductionOrderServiceImp orderService;

    private Product product;
    private RawMaterial material1;
    private RawMaterial material2;
    private BillOfMaterial bill1;
    private BillOfMaterial bill2;
    private ProductionOrder order;
    private ProductionOrderRequestDTO orderRequest;
    private ProductionOrderResponseDTO orderResponse;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Gaming Laptop");
        product.setProductionTime(8); // 8 hours per unit
        product.setCost(450.00);
        product.setStock(10);

        // Create raw materials
        material1 = new RawMaterial();
        material1.setId(1L);
        material1.setName("Plastic Resin");
        material1.setStock(500); // Sufficient stock
        material1.setUnitCost(15.50);

        material2 = new RawMaterial();
        material2.setId(2L);
        material2.setName("LCD Screen");
        material2.setStock(100); // Sufficient stock
        material2.setUnitCost(85.00);

        bill1 = new BillOfMaterial();
        bill1.setId(1L);
        bill1.setProduct(product);
        bill1.setMaterial(material1);
        bill1.setQuantity(5); // Need 5 kg plastic per laptop

        bill2 = new BillOfMaterial();
        bill2.setId(2L);
        bill2.setProduct(product);
        bill2.setMaterial(material2);
        bill2.setQuantity(1);

        product.setBills(Arrays.asList(bill1, bill2));

        order = new ProductionOrder();
        order.setId(1L);
        order.setProduct(product);
        order.setQuantity(10); // Want to produce 10 laptops
        order.setStatus(ProductionStatus.IN_WAITING);
        order.setPriority(1);

        System.out.println("Production order: " + order);

        // Create request DTO
        orderRequest = new ProductionOrderRequestDTO();
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(10);
        orderRequest.setPriority(1);

        // Create response DTO
        orderResponse = new ProductionOrderResponseDTO();
        orderResponse.setId(1L);
        orderResponse.setQuantity(10);
        orderResponse.setStatus(ProductionStatus.IN_WAITING);;
    }


    @Nested
    @DisplayName("Create production order tests")
    class CreateOrderTests {
        @Test
        @DisplayName("Should create production order successfully")
        void shouldCreateOrder() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderMapper.toEntity(any(ProductionOrderRequestDTO.class))).thenReturn(new ProductionOrder());
            when(orderRepository.save(any(ProductionOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(ProductionOrder.class)))
                    .thenReturn(orderResponse);

            ProductionOrderResponseDTO result = orderService.createOrder(orderRequest);
            System.out.println("Result: " + result);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatus()).isEqualTo(ProductionStatus.IN_WAITING);
            assertThat(result.getQuantity()).isEqualTo(10);

            verify(productRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(ProductionOrder.class));
            verify(orderMapper, times(1)).toResponseDTO(any(ProductionOrder.class));
        }
    }

    @Nested
    @DisplayName("Start production tests")
    class StartProductionTests {
        @Test
        @DisplayName("Should start production successfully and update material stock and order status")
        void shouldStartProduction() {

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(bomRepository.findByProductId(1L)).thenReturn(Arrays.asList(bill1, bill2));
            when(orderRepository.save(any(ProductionOrder.class))).thenReturn(order);
            when(orderMapper.toResponseDTO(any(ProductionOrder.class))).thenReturn(orderResponse);

            int material1InitialStock = material1.getStock(); // 500
            int material2InitialStock = material2.getStock(); // 100

            // ========== WHEN ==========
            ProductionOrderResponseDTO result = orderService.startProduction(1L);

            // ========== THEN ==========
            assertThat(result).isNotNull();

            // Verify material stock was reduced
            // For 10 laptops: need 10 × 5 = 50 kg plastic
            assertThat(material1.getStock()).isEqualTo(material1InitialStock - 50);
            // For 10 laptops: need 10 × 1 = 10 screens
            assertThat(material2.getStock()).isEqualTo(material2InitialStock - 10);

            // Verify order status changed
            verify(orderRepository, times(1)).save(any(ProductionOrder.class));

            // Use ArgumentCaptor to verify the exact order that was saved
            ArgumentCaptor<ProductionOrder> orderCaptor = ArgumentCaptor.forClass(ProductionOrder.class);
            verify(orderRepository).save(orderCaptor.capture());
            ProductionOrder savedOrder = orderCaptor.getValue();

            assertThat(savedOrder.getStatus()).isEqualTo(ProductionStatus.IN_PRODUCTION);
            assertThat(savedOrder.getStartDate()).isNotNull();
            assertThat(savedOrder.getEndDate()).isNotNull();

            // Verify production time calculation
            // 10 units × 8 hours = 80 hours ≈ 10 days
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                    savedOrder.getStartDate(),
                    savedOrder.getEndDate()
            );
            assertThat(daysBetween).isEqualTo(10);
        }
    }
}
