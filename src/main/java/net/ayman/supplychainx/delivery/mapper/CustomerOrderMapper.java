package net.ayman.supplychainx.delivery.mapper;

import net.ayman.supplychainx.delivery.dto.customer.CustomerRequestDTO;
import net.ayman.supplychainx.delivery.dto.customer.CustomerResponseDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderRequestDTO;
import net.ayman.supplychainx.delivery.dto.order.CustomerOrderResponseDTO;
import net.ayman.supplychainx.delivery.model.Address;
import net.ayman.supplychainx.delivery.model.Customer;
import net.ayman.supplychainx.delivery.model.CustomerOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, CustomerOrderItemMapper.class, AddressMapper.class})
public interface CustomerOrderMapper {
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    CustomerOrderResponseDTO toResponseDTO(CustomerOrder customerOrder);

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "shippingAddress", source = "addressId")
    @Mapping(target = "customer", source = "customerId")
    CustomerOrder toEntity(CustomerOrderRequestDTO dto);

    default Customer map(Long id) {
        if (id == null) return null;
        Customer c = new Customer();
        c.setId(id);
        return c;
    }

    default CustomerOrderResponseDTO.CustomerResp map(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerOrderResponseDTO.CustomerResp resp = new CustomerOrderResponseDTO.CustomerResp();
        resp.setId(customer.getId());
        resp.setName(customer.getName());
        resp.setEmail(customer.getEmail());
        resp.setPhone(customer.getPhone());
        return resp;
    }

    default Address mapAddrees(Long id) {
        if (id == null) {
            return null;
        }

        Address address = new Address();
        address.setId(id);
        return address;
    }
}
