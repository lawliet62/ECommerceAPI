package org.example.ecommerceapi.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryUpdateRequest(
        @NotNull @PositiveOrZero Integer stock
) {
}