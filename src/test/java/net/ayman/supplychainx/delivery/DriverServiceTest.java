package net.ayman.supplychainx.delivery;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.driver.DriverRequestDTO;
import net.ayman.supplychainx.delivery.dto.driver.DriverResponseDTO;
import net.ayman.supplychainx.delivery.mapper.DriverMapper;
import net.ayman.supplychainx.delivery.model.Driver;
import net.ayman.supplychainx.delivery.repository.DriverRepository;
import net.ayman.supplychainx.delivery.service.DriverServiceImp;
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
@DisplayName("Driver Service Tests")
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImp driverService;

    private Driver driver;
    private DriverRequestDTO requestDTO;
    private DriverResponseDTO responseDTO;

    private static final Long DRIVER_ID = 1L;
    private static final String DRIVER_NAME = "John Smith";
    private static final String PHONE = "+1234567890";
    private static final String LICENSE_NUMBER = "DL123456";
    private static final Boolean IS_AVAILABLE = true;

    @BeforeEach
    void setUp() {
        driver = new Driver();
        driver.setId(DRIVER_ID);
        driver.setName(DRIVER_NAME);
        driver.setPhone(PHONE);
        driver.setLicenseNumber(LICENSE_NUMBER);
        driver.setIsAvailable(IS_AVAILABLE);

        requestDTO = new DriverRequestDTO(DRIVER_NAME, PHONE, LICENSE_NUMBER, IS_AVAILABLE);

        responseDTO = new DriverResponseDTO(DRIVER_NAME, PHONE, LICENSE_NUMBER, IS_AVAILABLE);
    }

    @Nested
    @DisplayName("Add Driver Tests")
    class AddDriverTests {

        @Test
        @DisplayName("Should add new driver successfully")
        void shouldAddNewDriver() {
            when(driverMapper.toEntity(requestDTO)).thenReturn(driver);
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            when(driverMapper.toResponseDTO(any(Driver.class))).thenReturn(responseDTO);

            DriverResponseDTO result = driverService.addNewDriver(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(DRIVER_NAME);
            assertThat(result.phone()).isEqualTo(PHONE);
            assertThat(result.licenseNumber()).isEqualTo(LICENSE_NUMBER);
            assertThat(result.isAvailable()).isEqualTo(IS_AVAILABLE);

            verify(driverMapper, times(1)).toEntity(requestDTO);
            verify(driverRepository, times(1)).save(any(Driver.class));
            verify(driverMapper, times(1)).toResponseDTO(any(Driver.class));
        }

        @Test
        @DisplayName("Should save driver with correct data")
        void shouldSaveDriverWithCorrectData() {
            when(driverMapper.toEntity(requestDTO)).thenReturn(driver);
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            when(driverMapper.toResponseDTO(any(Driver.class))).thenReturn(responseDTO);

            driverService.addNewDriver(requestDTO);

            ArgumentCaptor<Driver> driverCaptor = ArgumentCaptor.forClass(Driver.class);
            verify(driverRepository).save(driverCaptor.capture());
            Driver savedDriver = driverCaptor.getValue();

            assertThat(savedDriver.getName()).isEqualTo(DRIVER_NAME);
            assertThat(savedDriver.getPhone()).isEqualTo(PHONE);
            assertThat(savedDriver.getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        }
    }

    @Nested
    @DisplayName("Get Driver Tests")
    class GetDriverTests {

        @Test
        @DisplayName("Should get driver by id successfully")
        void shouldGetDriverById() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            when(driverMapper.toResponseDTO(any(Driver.class))).thenReturn(responseDTO);

            DriverResponseDTO result = driverService.getDriverById(DRIVER_ID);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(DRIVER_NAME);

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverMapper, times(1)).toResponseDTO(any(Driver.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when driver not found")
        void shouldThrowExceptionWhenDriverNotFound() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.getDriverById(DRIVER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Driver with id " + DRIVER_ID + " not found");

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverMapper, never()).toResponseDTO(any());
        }

        @Test
        @DisplayName("Should get all drivers successfully")
        void shouldGetAllDrivers() {
            Driver driver2 = new Driver();
            driver2.setId(2L);
            driver2.setName("Jane Doe");
            driver2.setPhone("+0987654321");
            driver2.setLicenseNumber("DL654321");
            driver2.setIsAvailable(false);

            DriverResponseDTO responseDTO2 = new DriverResponseDTO("Jane Doe", "+0987654321", "DL654321", false);

            List<Driver> drivers = Arrays.asList(driver, driver2);

            when(driverRepository.findAll()).thenReturn(drivers);
            when(driverMapper.toResponseDTO(driver)).thenReturn(responseDTO);
            when(driverMapper.toResponseDTO(driver2)).thenReturn(responseDTO2);

            List<DriverResponseDTO> result = driverService.getAllDrivers();

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);

            verify(driverRepository, times(1)).findAll();
            verify(driverMapper, times(2)).toResponseDTO(any(Driver.class));
        }

        @Test
        @DisplayName("Should return empty list when no drivers exist")
        void shouldReturnEmptyListWhenNoDrivers() {
            when(driverRepository.findAll()).thenReturn(Arrays.asList());

            List<DriverResponseDTO> result = driverService.getAllDrivers();

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(0);

            verify(driverRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Update Driver Tests")
    class UpdateDriverTests {

        @Test
        @DisplayName("Should update driver info successfully")
        void shouldUpdateDriverInfo() {
            DriverRequestDTO updateDTO = new DriverRequestDTO("Updated Name", "+9999999999", "DL999999", true);

            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            doNothing().when(driverMapper).updateDriver(updateDTO, driver);
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            when(driverMapper.toResponseDTO(any(Driver.class))).thenReturn(responseDTO);

            DriverResponseDTO result = driverService.updateDriverInfo(DRIVER_ID, updateDTO);

            assertThat(result).isNotNull();

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverMapper, times(1)).updateDriver(updateDTO, driver);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, times(1)).toResponseDTO(any(Driver.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existing driver")
        void shouldThrowExceptionWhenUpdatingNonExistingDriver() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.updateDriverInfo(DRIVER_ID, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Driver with id " + DRIVER_ID + " not found");

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should call mapper to update driver")
        void shouldCallMapperToUpdateDriver() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            doNothing().when(driverMapper).updateDriver(requestDTO, driver);
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            when(driverMapper.toResponseDTO(any(Driver.class))).thenReturn(responseDTO);

            driverService.updateDriverInfo(DRIVER_ID, requestDTO);

            ArgumentCaptor<DriverRequestDTO> dtoCaptor = ArgumentCaptor.forClass(DriverRequestDTO.class);
            ArgumentCaptor<Driver> driverCaptor = ArgumentCaptor.forClass(Driver.class);
            verify(driverMapper).updateDriver(dtoCaptor.capture(), driverCaptor.capture());

            assertThat(dtoCaptor.getValue()).isEqualTo(requestDTO);
            assertThat(driverCaptor.getValue()).isEqualTo(driver);
        }
    }

    @Nested
    @DisplayName("Delete Driver Tests")
    class DeleteDriverTests {

        @Test
        @DisplayName("Should delete driver successfully")
        void shouldDeleteDriver() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            doNothing().when(driverRepository).delete(driver);

            driverService.deleteDriver(DRIVER_ID);

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverRepository, times(1)).delete(driver);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing driver")
        void shouldThrowExceptionWhenDeletingNonExistingDriver() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.deleteDriver(DRIVER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Driver with id " + DRIVER_ID + " not found");

            verify(driverRepository, times(1)).findById(DRIVER_ID);
            verify(driverRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should find driver before deleting")
        void shouldFindDriverBeforeDeleting() {
            when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));
            doNothing().when(driverRepository).delete(driver);

            driverService.deleteDriver(DRIVER_ID);

            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            verify(driverRepository).findById(idCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DRIVER_ID);
        }
    }
}
