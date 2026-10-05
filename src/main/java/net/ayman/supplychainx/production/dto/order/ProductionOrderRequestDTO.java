package net.ayman.supplychainx.production.dto.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionOrderRequestDTO {
    @NotNull
    private Long productId;
    @NotNull
    @Positive
    private Integer quantity;
    @Min(value = 1)
    @Max(value = 5)
    private Integer priority;
    private LocalDateTime startDate;
}
