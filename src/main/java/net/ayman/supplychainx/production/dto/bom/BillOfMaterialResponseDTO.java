package net.ayman.supplychainx.production.dto.bom;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillOfMaterialResponseDTO {
    private Long id;
    private Long materialId;
    private String materialName;
    private Long productId;
    private String productName;
    private String materialUnit;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
}
