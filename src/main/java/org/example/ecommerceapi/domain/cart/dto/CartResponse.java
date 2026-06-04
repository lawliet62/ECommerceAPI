package org.example.ecommerceapi.domain.cart.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items
) {
}