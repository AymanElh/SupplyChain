package net.ayman.supplychainx.delivery;

import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleRequestDTO;
import net.ayman.supplychainx.delivery.dto.vehicle.VehicleResponseDTO;
import net.ayman.supplychainx.delivery.mapper.VehicleMapper;
import net.ayman.supplychainx.delivery.model.Vehicle;
import net.ayman.supplychainx.delivery.repository.VehicleRepository;
import net.ayman.supplychainx.delivery.service.VehicleServiceImp;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vehicle Service Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImp vehicleService;

    private Vehicle vehicle;
    private VehicleRequestDTO requestDTO;
    private VehicleResponseDTO responseDTO;

    private static final Long VEHICLE_ID = 1L;
    private static final String LICENSE_PLATE = "ABC-123";
    private static final String TYPE = "Truck";
    private static final String MODEL = "Ford F-150";

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(VEHICLE_ID);
        vehicle.setLicensePlate(LICENSE_PLATE);
        vehicle.setType(TYPE);
        vehicle.setModel(MODEL);

        requestDTO = new VehicleRequestDTO(LICENSE_PLATE, TYPE, MODEL);

        responseDTO = new VehicleResponseDTO(VEHICLE_ID, LICENSE_PLATE, TYPE, MODEL);
    }

    @Nested
    @DisplayName("Create Vehicle Tests")
    class CreateVehicleTests {

        @Test
        @DisplayName("Should create vehicle successfully")
        void shouldCreateVehicle() {
            when(vehicleMapper.toEntity(requestDTO)).thenReturn(vehicle);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            VehicleResponseDTO result = vehicleService.createVehicle(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(VEHICLE_ID);
            assertThat(result.licensePlate()).isEqualTo(LICENSE_PLATE);
            assertThat(result.type()).isEqualTo(TYPE);
            assertThat(result.model()).isEqualTo(MODEL);

            verify(vehicleMapper, times(1)).toEntity(requestDTO);
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
            verify(vehicleMapper, times(1)).toResponseDTO(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should save vehicle with correct data")
        void shouldSaveVehicleWithCorrectData() {
            when(vehicleMapper.toEntity(requestDTO)).thenReturn(vehicle);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            vehicleService.createVehicle(requestDTO);

            ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
            verify(vehicleRepository).save(vehicleCaptor.capture());
            Vehicle savedVehicle = vehicleCaptor.getValue();

            assertThat(savedVehicle.getLicensePlate()).isEqualTo(LICENSE_PLATE);
            assertThat(savedVehicle.getType()).isEqualTo(TYPE);
            assertThat(savedVehicle.getModel()).isEqualTo(MODEL);
        }
    }

    @Nested
    @DisplayName("Get Vehicle Tests")
    class GetVehicleTests {

        @Test
        @DisplayName("Should get vehicle by id successfully")
        void shouldGetVehicleById() {
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            VehicleResponseDTO result = vehicleService.getVehicleById(VEHICLE_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(VEHICLE_ID);
            assertThat(result.licensePlate()).isEqualTo(LICENSE_PLATE);

            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(vehicleMapper, times(1)).toResponseDTO(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when vehicle not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.getVehicleById(VEHICLE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Vehicle with id " + VEHICLE_ID + " not found");

            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(vehicleMapper, never()).toResponseDTO(any());
        }

        @Test
        @DisplayName("Should get all vehicles with pagination")
        void shouldGetAllVehicles() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));

            when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            Page<VehicleResponseDTO> result = vehicleService.getAllVehicles(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(vehicleRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Update Vehicle Tests")
    class UpdateVehicleTests {

        @Test
        @DisplayName("Should update vehicle successfully")
        void shouldUpdateVehicle() {
            VehicleRequestDTO updateDTO = new VehicleRequestDTO("XYZ-789", "Van", "Mercedes Sprinter");

            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
            doNothing().when(vehicleMapper).updateEntityFromDto(updateDTO, vehicle);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            VehicleResponseDTO result = vehicleService.updateVehicle(VEHICLE_ID, updateDTO);

            assertThat(result).isNotNull();

            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(vehicleMapper, times(1)).updateEntityFromDto(updateDTO, vehicle);
            verify(vehicleRepository, times(1)).save(vehicle);
            verify(vehicleMapper, times(1)).toResponseDTO(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existing vehicle")
        void shouldThrowExceptionWhenUpdatingNonExistingVehicle() {
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.updateVehicle(VEHICLE_ID, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Vehicle with id " + VEHICLE_ID + " not found");

            verify(vehicleRepository, times(1)).findById(VEHICLE_ID);
            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should call mapper to update entity from DTO")
        void shouldCallMapperToUpdateEntity() {
            when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
            doNothing().when(vehicleMapper).updateEntityFromDto(requestDTO, vehicle);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponseDTO(any(Vehicle.class))).thenReturn(responseDTO);

            vehicleService.updateVehicle(VEHICLE_ID, requestDTO);

            ArgumentCaptor<VehicleRequestDTO> dtoCaptor = ArgumentCaptor.forClass(VehicleRequestDTO.class);
            ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
            verify(vehicleMapper).updateEntityFromDto(dtoCaptor.capture(), vehicleCaptor.capture());

            assertThat(dtoCaptor.getValue()).isEqualTo(requestDTO);
            assertThat(vehicleCaptor.getValue()).isEqualTo(vehicle);
        }
    }

    @Nested
    @DisplayName("Delete Vehicle Tests")
    class DeleteVehicleTests {

        @Test
        @DisplayName("Should delete vehicle successfully")
        void shouldDeleteVehicle() {
            when(vehicleRepository.existsById(VEHICLE_ID)).thenReturn(true);
            doNothing().when(vehicleRepository).deleteById(VEHICLE_ID);

            vehicleService.deleteVehicle(VEHICLE_ID);

            verify(vehicleRepository, times(1)).existsById(VEHICLE_ID);
            verify(vehicleRepository, times(1)).deleteById(VEHICLE_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing vehicle")
        void shouldThrowExceptionWhenDeletingNonExistingVehicle() {
            when(vehicleRepository.existsById(VEHICLE_ID)).thenReturn(false);

            assertThatThrownBy(() -> vehicleService.deleteVehicle(VEHICLE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Vehicle with id " + VEHICLE_ID + " not found");

            verify(vehicleRepository, times(1)).existsById(VEHICLE_ID);
            verify(vehicleRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should check existence before deleting")
        void shouldCheckExistenceBeforeDeleting() {
            when(vehicleRepository.existsById(VEHICLE_ID)).thenReturn(true);
            doNothing().when(vehicleRepository).deleteById(VEHICLE_ID);

            vehicleService.deleteVehicle(VEHICLE_ID);

            verify(vehicleRepository, times(1)).existsById(VEHICLE_ID);
        }
    }
}
