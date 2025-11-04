package net.ayman.supplychainx.delivery.dto.order;

import lombok.Data;
import net.ayman.supplychainx.delivery.dto.OrderItem.CustomerOrderItemResponseDTO;
import net.ayman.supplychainx.delivery.dto.address.AddressResponseDTO;
import net.ayman.supplychainx.delivery.model.Address;
import net.ayman.supplychainx.delivery.model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Long id;
    private Integer quantity;
    private CustomerResp customer;
    private List<CustomerOrderItemResponseDTO> orderItems;
    private OrderStatus status;
    private LocalDate orderDate;
    private Double totalAmount;
    private AddressResponseDTO shippingAddress;

    @Data
    public static class CustomerResp {
        private Long id;
        private String name;
        private String phone;
        private String email;
    }
}
