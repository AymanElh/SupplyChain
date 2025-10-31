package net.ayman.supplychainx.supply.dto.rawmaterial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialResponse {
    private Long id;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;
    private Boolean isCritical;
}
