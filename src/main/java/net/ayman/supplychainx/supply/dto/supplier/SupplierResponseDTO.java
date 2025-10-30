package net.ayman.supplychainx.supply.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Double rating;
    private Integer leadTime;
}
