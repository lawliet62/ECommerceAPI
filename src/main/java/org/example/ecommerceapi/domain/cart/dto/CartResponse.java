package org.example.ecommerceapi.domain.cart.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items
) {
    public static CartResponse of(Long cartId, List<CartItemResponse> items) {
        return new CartResponse(cartId, items);
    }

    public static CartResponse empty() {
        return new CartResponse(null, List.of());
    }
}