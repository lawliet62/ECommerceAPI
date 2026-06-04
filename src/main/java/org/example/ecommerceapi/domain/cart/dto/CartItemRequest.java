package org.example.ecommerceapi.domain.cart.dto;

public record CartItemRequest(
        Long productId,
        int quantity
) {
}