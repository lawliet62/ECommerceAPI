package org.example.ecommerceapi.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @PositiveOrZero Integer stock
) {
}