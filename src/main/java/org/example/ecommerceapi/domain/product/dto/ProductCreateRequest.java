package org.example.ecommerceapi.domain.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @PositiveOrZero Integer stock
) {
}