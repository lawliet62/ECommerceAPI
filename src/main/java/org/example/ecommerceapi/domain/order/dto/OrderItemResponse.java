package org.example.ecommerceapi.domain.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long orderItemId,
        String productName,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {
}
