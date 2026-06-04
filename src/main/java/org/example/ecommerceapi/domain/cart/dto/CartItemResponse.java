package org.example.ecommerceapi.domain.cart.dto;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        int quantity
) {
}