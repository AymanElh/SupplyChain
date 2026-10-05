package net.ayman.supplychainx.production.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProductQuantityDTO(@NotNull @Min(value = 1) Integer quantity) {}
