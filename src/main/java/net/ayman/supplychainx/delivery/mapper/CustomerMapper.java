package net.ayman.supplychainx.delivery.mapper;

import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    CustomerResponseDTO toResponseDTO(Customer customer);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", source = "addresses")
    Customer toEntity(CustomerRequestDTO customerRequestDTO);

    void updateEntityToDTO(CustomerRequestDTO customerRequestDTO, @MappingTarget Customer customer);
}
