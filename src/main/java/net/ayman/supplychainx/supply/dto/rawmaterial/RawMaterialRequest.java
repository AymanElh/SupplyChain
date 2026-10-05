package net.ayman.supplychainx.supply.dto.rawmaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class RawMaterialRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull
    @Positive(message = "Stock must be greater than 0")
    private Integer stock;
    @NotNull
    @Positive(message = "Stock min must be greater than 0")
    private Integer stockMin;
    @NotNull
    @Positive
    private Double unitCost;
    @NotBlank(message = "Unit is required")
    private String unit;
    @NotNull
    private List<Long> supplierIds;
}
