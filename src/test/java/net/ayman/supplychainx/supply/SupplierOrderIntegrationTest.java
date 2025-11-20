package net.ayman.supplychainx.supply;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ayman.supplychainx.AbstractIntegrationTest;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.model.Supplier;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import net.ayman.supplychainx.supply.repository.SupplierOrderRepository;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SupplierOrderIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SupplierOrderRepository supplyOrderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RawMaterialRepository rawMaterialRepository;
    @Autowired
    SupplierRepository supplierRepository;

    private Supplier supplier;
    private RawMaterial rawMaterial;

    private static final String ADMIN_EMAIL = "admin@gmail.com";

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmail(ADMIN_EMAIL).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        supplier = new Supplier();
        supplier.setName("SupplierTest");
        supplier.setRating(3.0);
        supplier.setEmail("supp@gmail.com");
        supplier.setLeadTime(10);
        supplierRepository.save(supplier);

        rawMaterial = new RawMaterial();
        rawMaterial.setStock(20);
        rawMaterial.setName("matiere 1");
        rawMaterial.setUnit("kg");
        rawMaterial.setStockMin(5);
        rawMaterialRepository.save(rawMaterial);
    }


    @AfterEach
    void clean() {
        supplyOrderRepository.deleteAll();
        supplierRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void testCreateOrder_Successfully() throws JsonProcessingException {
        SupplierOrderItemRequestDTO itemDto = new SupplierOrderItemRequestDTO();
        itemDto.setMaterialId(itemDto.getMaterialId());
        itemDto.setQuantity(20);
        itemDto.setUnitPrice(50.0);

        SupplierOrderRequestDTO orderDto = new SupplierOrderRequestDTO();
        orderDto.setSupplierId(supplier.getId());
        orderDto.setOrderDate(LocalDate.now());
        orderDto.setItems(List.of(itemDto));

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierId", is(1)))
                .andExpect(jsonPath("$.orderItems.length()", is(1));
    }

}
