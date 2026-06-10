package org.example.ecommerceapi.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price
) {
}
