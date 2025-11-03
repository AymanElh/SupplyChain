package net.ayman.supplychainx.supply.dto.supplier;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ayman.supplychainx.validation.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDTO {
    @NotBlank(message = "Name is required", groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String phone;
    private String email;
    @NotNull(groups = OnCreate.class)
    @Min(value = 0, message = "Rating shouldn't be less than 0", groups = OnCreate.class)
    @Max(value = 5, message = "Rating must be between 0 and 5")
    private Double rating;
    @NotNull(groups = OnCreate.class)
    @Positive(message = "Lead time must be positive")
    private Integer leadTime;
}
