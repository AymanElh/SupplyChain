package net.ayman.supplychainx.production;

import net.ayman.supplychainx.common.exception.DuplicateResourceException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialRequestDTO;
import net.ayman.supplychainx.production.dto.bom.BillOfMaterialResponseDTO;
import net.ayman.supplychainx.production.mapper.BillOfMaterialMapper;
import net.ayman.supplychainx.production.model.BillOfMaterial;
import net.ayman.supplychainx.production.model.Product;
import net.ayman.supplychainx.production.repository.BillOfMaterialRepository;
import net.ayman.supplychainx.production.repository.ProductRepository;
import net.ayman.supplychainx.production.service.BillOfMaterialServiceImp;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Bill of Material Service Tests")
class BillOfMaterialServiceTest {

    @Mock
    private BillOfMaterialRepository billOfMaterialRepository;
    @Mock
    private BillOfMaterialMapper billOfMaterialMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private BillOfMaterialServiceImp billOfMaterialService;

    private Product product;
    private RawMaterial rawMaterial;
    private BillOfMaterial billOfMaterial;
    private BillOfMaterialRequestDTO requestDTO;
    private BillOfMaterialResponseDTO responseDTO;

    private static final Long PRODUCT_ID = 1L;
    private static final Long MATERIAL_ID = 1L;
    private static final Long BOM_ID = 1L;
    private static final Integer QUANTITY = 5;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Gaming Laptop");

        rawMaterial = new RawMaterial();
        rawMaterial.setId(MATERIAL_ID);
        rawMaterial.setName("LCD Screen");
        rawMaterial.setUnitCost(85.00);

        billOfMaterial = new BillOfMaterial();
        billOfMaterial.setId(BOM_ID);
        billOfMaterial.setProduct(product);
        billOfMaterial.setMaterial(rawMaterial);
        billOfMaterial.setQuantity(QUANTITY);

        requestDTO = new BillOfMaterialRequestDTO();
        requestDTO.setMaterialId(MATERIAL_ID);
        requestDTO.setQuantity(QUANTITY);

        responseDTO = new BillOfMaterialResponseDTO();
        responseDTO.setId(BOM_ID);
        responseDTO.setProductId(PRODUCT_ID);
        responseDTO.setMaterialId(MATERIAL_ID);
        responseDTO.setQuantity(QUANTITY);
        responseDTO.setMaterialName("LCD Screen");
        responseDTO.setUnitCost(85.00);
    }

    @Nested
    @DisplayName("Add Material to Product Tests")
    class AddMaterialToProductTests {
        
        @Test
        @DisplayName("Should add material to product successfully")
        void shouldAddMaterialToProduct() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(rawMaterial));
            when(billOfMaterialRepository.existsByProductIdAndMaterialId(PRODUCT_ID, MATERIAL_ID))
                    .thenReturn(false);
            when(billOfMaterialRepository.save(any(BillOfMaterial.class))).thenReturn(billOfMaterial);
            when(billOfMaterialMapper.toResponseDTO(billOfMaterial)).thenReturn(responseDTO);

            BillOfMaterialResponseDTO result = billOfMaterialService.addMaterialToProduct(PRODUCT_ID, requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(BOM_ID);
            assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(result.getMaterialId()).isEqualTo(MATERIAL_ID);
            assertThat(result.getQuantity()).isEqualTo(QUANTITY);

            verify(productRepository, times(1)).findById(PRODUCT_ID);
            verify(rawMaterialRepository, times(1)).findById(MATERIAL_ID);
            verify(billOfMaterialRepository, times(1)).existsByProductIdAndMaterialId(PRODUCT_ID, MATERIAL_ID);
            verify(billOfMaterialRepository, times(1)).save(any(BillOfMaterial.class));
            verify(billOfMaterialMapper, times(1)).toResponseDTO(billOfMaterial);

            ArgumentCaptor<BillOfMaterial> bomCaptor = ArgumentCaptor.forClass(BillOfMaterial.class);
            verify(billOfMaterialRepository).save(bomCaptor.capture());
            BillOfMaterial savedBom = bomCaptor.getValue();
            
            assertThat(savedBom.getProduct()).isEqualTo(product);
            assertThat(savedBom.getMaterial()).isEqualTo(rawMaterial);
            assertThat(savedBom.getQuantity()).isEqualTo(QUANTITY);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billOfMaterialService.addMaterialToProduct(PRODUCT_ID, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product with id " + PRODUCT_ID + " not found");

            verify(productRepository, times(1)).findById(PRODUCT_ID);
            verify(rawMaterialRepository, never()).findById(any());
            verify(billOfMaterialRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when material not found")
        void shouldThrowExceptionWhenMaterialNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billOfMaterialService.addMaterialToProduct(PRODUCT_ID, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Material with this id " + MATERIAL_ID + " not found");

            verify(productRepository, times(1)).findById(PRODUCT_ID);
            verify(rawMaterialRepository, times(1)).findById(MATERIAL_ID);
            verify(billOfMaterialRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when bill of material already exists")
        void shouldThrowExceptionWhenBillOfMaterialAlreadyExists() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(rawMaterial));
            when(billOfMaterialRepository.existsByProductIdAndMaterialId(PRODUCT_ID, MATERIAL_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> billOfMaterialService.addMaterialToProduct(PRODUCT_ID, requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("This bill of material is already exist");

            verify(productRepository, times(1)).findById(PRODUCT_ID);
            verify(rawMaterialRepository, times(1)).findById(MATERIAL_ID);
            verify(billOfMaterialRepository, times(1)).existsByProductIdAndMaterialId(PRODUCT_ID, MATERIAL_ID);
            verify(billOfMaterialRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Product Bill Tests")
    class GetProductBillTests {
        
        @Test
        @DisplayName("Should return list of bill of materials for product")
        void shouldGetProductBill() {
            RawMaterial material2 = new RawMaterial();
            material2.setId(2L);
            material2.setName("Plastic Resin");
            material2.setUnitCost(15.50);

            BillOfMaterial bom2 = new BillOfMaterial();
            bom2.setId(2L);
            bom2.setProduct(product);
            bom2.setMaterial(material2);
            bom2.setQuantity(3);

            BillOfMaterialResponseDTO responseDTO2 = new BillOfMaterialResponseDTO();
            responseDTO2.setId(2L);
            responseDTO2.setProductId(PRODUCT_ID);
            responseDTO2.setMaterialId(2L);
            responseDTO2.setQuantity(3);

            List<BillOfMaterial> billOfMaterials = Arrays.asList(billOfMaterial, bom2);

            when(billOfMaterialRepository.findByProductId(PRODUCT_ID)).thenReturn(billOfMaterials);
            when(billOfMaterialMapper.toResponseDTO(billOfMaterial)).thenReturn(responseDTO);
            when(billOfMaterialMapper.toResponseDTO(bom2)).thenReturn(responseDTO2);

            List<BillOfMaterialResponseDTO> result = billOfMaterialService.getProductBill(PRODUCT_ID);

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);

            verify(billOfMaterialRepository, times(1)).findByProductId(PRODUCT_ID);
            verify(billOfMaterialMapper, times(2)).toResponseDTO(any(BillOfMaterial.class));
        }

        @Test
        @DisplayName("Should return empty list when no bill of materials found for product")
        void shouldReturnEmptyListWhenNoBillOfMaterialsFound() {
            when(billOfMaterialRepository.findByProductId(PRODUCT_ID)).thenReturn(Arrays.asList());

            List<BillOfMaterialResponseDTO> result = billOfMaterialService.getProductBill(PRODUCT_ID);

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(0);

            verify(billOfMaterialRepository, times(1)).findByProductId(PRODUCT_ID);
            verify(billOfMaterialMapper, never()).toResponseDTO(any());
        }
    }

    @Nested
    @DisplayName("Update Quantity Tests")
    class UpdateQuantityTests {
        
        @Test
        @DisplayName("Should update quantity successfully")
        void shouldUpdateQuantity() {
            Integer newQuantity = 10;
            BillOfMaterial updatedBom = new BillOfMaterial();
            updatedBom.setId(BOM_ID);
            updatedBom.setProduct(product);
            updatedBom.setMaterial(rawMaterial);
            updatedBom.setQuantity(newQuantity);

            BillOfMaterialResponseDTO updatedResponseDTO = new BillOfMaterialResponseDTO();
            updatedResponseDTO.setId(BOM_ID);
            updatedResponseDTO.setQuantity(newQuantity);

            when(billOfMaterialRepository.findById(BOM_ID)).thenReturn(Optional.of(billOfMaterial));
            when(billOfMaterialRepository.save(any(BillOfMaterial.class))).thenReturn(updatedBom);
            when(billOfMaterialMapper.toResponseDTO(updatedBom)).thenReturn(updatedResponseDTO);

            BillOfMaterialResponseDTO result = billOfMaterialService.updateQuantity(BOM_ID, newQuantity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(BOM_ID);
            assertThat(result.getQuantity()).isEqualTo(newQuantity);

            verify(billOfMaterialRepository, times(1)).findById(BOM_ID);
            verify(billOfMaterialRepository, times(1)).save(any(BillOfMaterial.class));
            verify(billOfMaterialMapper, times(1)).toResponseDTO(updatedBom);

            ArgumentCaptor<BillOfMaterial> bomCaptor = ArgumentCaptor.forClass(BillOfMaterial.class);
            verify(billOfMaterialRepository).save(bomCaptor.capture());
            BillOfMaterial savedBom = bomCaptor.getValue();
            
            assertThat(savedBom.getQuantity()).isEqualTo(newQuantity);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when bill of material not found")
        void shouldThrowExceptionWhenBillOfMaterialNotFound() {
            when(billOfMaterialRepository.findById(BOM_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billOfMaterialService.updateQuantity(BOM_ID, 10))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Bill of material with id " + BOM_ID + " not found");

            verify(billOfMaterialRepository, times(1)).findById(BOM_ID);
            verify(billOfMaterialRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Remove Material Tests")
    class RemoveMaterialTests {
        
        @Test
        @DisplayName("Should remove material successfully")
        void shouldRemoveMaterial() {
            when(billOfMaterialRepository.findById(BOM_ID)).thenReturn(Optional.of(billOfMaterial));
            doNothing().when(billOfMaterialRepository).delete(billOfMaterial);

            billOfMaterialService.removeMaterial(BOM_ID);

            verify(billOfMaterialRepository, times(1)).findById(BOM_ID);
            verify(billOfMaterialRepository, times(1)).delete(billOfMaterial);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when bill of material not found")
        void shouldThrowExceptionWhenBillOfMaterialNotFound() {
            when(billOfMaterialRepository.findById(BOM_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billOfMaterialService.removeMaterial(BOM_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Bill of material with this id " + BOM_ID + " not found");

            verify(billOfMaterialRepository, times(1)).findById(BOM_ID);
            verify(billOfMaterialRepository, never()).delete(any());
        }
    }
}
