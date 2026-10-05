package net.ayman.supplychainx.supply.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ayman.supplychainx.supply.dto.rawmaterial.RawMaterialResponse;

import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Double rating;
    private Integer leadTime;

    private List<RawMaterialResponse> materials;
}
