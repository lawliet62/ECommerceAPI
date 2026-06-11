package org.example.ecommerceapi.domain.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(
        @NotNull @Positive Long productId,
        @NotNull @Positive @Max(99) Integer quantity
) {
}