package net.ayman.supplychainx.production.dto.bom;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BillOfMaterialRequestDTO {
    @NotNull
    private Long materialId;
    @NotNull
    @Min(value = 1)
    private Integer quantity;
}
