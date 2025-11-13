package net.ayman.supplychainx.supply;

import net.ayman.supplychainx.supply.dto.supplier.SupplierRequestDTO;
import net.ayman.supplychainx.supply.dto.supplier.SupplierResponseDTO;
import net.ayman.supplychainx.supply.mapper.SupplierMapper;
import net.ayman.supplychainx.supply.model.Supplier;
import net.ayman.supplychainx.supply.repository.SupplierRepository;
import net.ayman.supplychainx.supply.service.SupplierServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {
    @Mock
    SupplierRepository supplierRepository;
    @Mock
    SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImp supplierService;

    private Supplier supplier;

    private SupplierRequestDTO supplierRequestDTO;
    private SupplierResponseDTO supplierResponseDTO;
    private static final Long SUPPLIER_ID = 1L;
    private static final String SUPPLIER_NAME = "Tech Supplies Co.";

    @BeforeEach
    void setUp() {
        supplierRequestDTO = SupplierRequestDTO.builder()
                .name(SUPPLIER_NAME)
                .email("contact@techsupplies.com")
                .phone("+1234567890")
                .leadTime(7)
                .rating(4.5)
                .build();

        supplierResponseDTO = SupplierResponseDTO.builder()
                .id(SUPPLIER_ID)
                .name(SUPPLIER_NAME)
                .email("contact@techsupplies.com")
                .phone("+1234567890")
                .leadTime(7)
                .rating(4.5)
                .build();


        supplier = Supplier.builder()
                .id(SUPPLIER_ID)
                .name(SUPPLIER_NAME)
                .email("contact@techsupplies.com")
                .phone("+1234567890")
                .leadTime(7)
                .rating(4.5)
                .build();
    }

    void shouldCreateSupplier() {
        SupplierRequestDTO requestWithNulls = SupplierRequestDTO.builder()
                .name("Test Supplier")
                .email(null)
                .phone(null)
                .leadTime(null)
                .rating(null)
                .build();

        Supplier supplierWithNulls = Supplier.builder()
                .id(2L)
                .name("Test Supplier")
                .email(null)
                .phone(null)
                .leadTime(null)
                .rating(null)
                .build();

        SupplierResponseDTO responseWithNulls = SupplierResponseDTO.builder()
                .id(2L)
                .name("Test Supplier")
                .email(null)
                .phone(null)
                .leadTime(null)
                .rating(null)
                .build();


    }
}
