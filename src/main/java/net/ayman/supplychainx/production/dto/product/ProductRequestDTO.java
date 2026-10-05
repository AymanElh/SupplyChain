package net.ayman.supplychainx.production.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Name product is required")
    private String name;
    @NotNull(message = "Production time is required")
    @Positive(message = "Production time is must be positive and in hours")
    private Integer productionTime; // in hours
    @NotNull(message = "Cost price is required")
    @Min(value = 0, message = "Cost cannot be under 0")
    private Double cost;
    @NotNull
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
