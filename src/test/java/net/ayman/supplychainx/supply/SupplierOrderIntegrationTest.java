package net.ayman.supplychainx.supply;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.ayman.supplychainx.AbstractIntegrationTest;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderItemRequestDTO;
import net.ayman.supplychainx.supply.dto.order.SupplierOrderRequestDTO;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.model.Supplier;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import net.ayman.supplychainx.supply.repository.SupplierOrderRepository;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import net.ayman.supplychainx.user.dto.login.LoginRequestDTO;
import net.ayman.supplychainx.user.dto.login.LoginResponseDTO;
import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.RoleRepository;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private Supplier supplier;
    private RawMaterial rawMaterial;
    private User user;

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "123456";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        user = userRepository.findByEmail(ADMIN_EMAIL).orElse(null);
        if (user == null) {
            Role role = roleRepository.findByName("RESPONSABLE_ACHATS").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("RESPONSABLE_ACHATS");
                return roleRepository.save(newRole);
            });
            
            user = new User();
            user.setEmail(ADMIN_EMAIL);
            user.setName("admin");
            user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            user.setRole(role);
            user = userRepository.save(user);
        }

        supplier = new Supplier();
        supplier.setName("SupplierTest");
        supplier.setRating(3.0);
        supplier.setEmail("supp@gmail.com");
        supplier.setPhone("2928392922");
        supplier.setLeadTime(10);
        supplierRepository.save(supplier);

        rawMaterial = new RawMaterial();
        rawMaterial.setStock(20);
        rawMaterial.setName("matiere 1");
        rawMaterial.setUnit("kg");
        rawMaterial.setStockMin(5);
        rawMaterial.setSuppliers(List.of(supplier));
        rawMaterialRepository.save(rawMaterial);
    }


    @AfterEach
    void clean() {
        jdbcTemplate.execute("DELETE FROM supply_order_items");
        jdbcTemplate.execute("DELETE FROM supplier_orders");
        jdbcTemplate.execute("DELETE FROM material_suppliers");
        jdbcTemplate.execute("DELETE FROM suppliers");
        jdbcTemplate.execute("DELETE FROM raw_materials");
    }


    @Test
    void testCreateOrder_Successfully() throws Exception {

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        var loginResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        SupplierOrderItemRequestDTO itemDto = new SupplierOrderItemRequestDTO();
        itemDto.setMaterialId(rawMaterial.getId());
        itemDto.setQuantity(20);
        itemDto.setUnitPrice(50.0);

        SupplierOrderRequestDTO orderDto = new SupplierOrderRequestDTO();
        orderDto.setSupplierId(supplier.getId());
        orderDto.setOrderDate(LocalDate.now());
        orderDto.setItems(List.of(itemDto));

        // Use the session from the login request
        mockMvc.perform(post("/api/v1/supplier-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session((org.springframework.mock.web.MockHttpSession) loginResult.getRequest().getSession())
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierId").value(supplier.getId()));
    }

}
