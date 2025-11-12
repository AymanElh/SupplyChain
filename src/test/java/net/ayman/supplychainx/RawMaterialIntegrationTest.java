package net.ayman.supplychainx;

import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialRequest;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;
import net.ayman.supplychainx.supply.mapper.RawMaterialMapper;
import net.ayman.supplychainx.supply.model.RawMaterial;
import net.ayman.supplychainx.supply.repository.RawMaterialRepository;
import net.ayman.supplychainx.supply.service.RawMaterialService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName(("Raw Material Integration Tests"))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RawMaterialIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RawMaterialService materialService;
    @Autowired
    private RawMaterialRepository materialRepository;

    private RawMaterialRequest materialRequest;

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll();

        materialRequest = RawMaterialRequest.builder()
                .name("Steel Alloy Type B")
                .unit("TON")
                .stock(200)
                .stockMin(50)
                .unitCost(1200.75)
                .unit("kg")
                .build();

    }

    @Test
    @Order(1)
    @DisplayName("Should save and find material")
    void shouldSaveAndFindMaterial() {
        RawMaterial material = new RawMaterial();
        material.setName("Test Material");
        material.setUnit("KG");
        material.setStock(1000);
        material.setStockMin(200);
        material.setUnitCost(20.00);

        RawMaterial savedMaterial = materialRepository.save(material);
        assertThat(savedMaterial.getId()).isNotNull();
        assertThat(savedMaterial.getId()).isNotNull(); // ID auto-generated
        assertThat(savedMaterial.getCreatedAt()).isNotNull(); // @PrePersist worked
        assertThat(savedMaterial.getUpdatedAt()).isNotNull();

        Optional<RawMaterial> found = materialRepository.findById(savedMaterial.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Material");
        assertThat(found.get().getStock()).isEqualTo(1000);
    }


}
